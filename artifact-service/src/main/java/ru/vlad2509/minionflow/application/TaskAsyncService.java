package ru.vlad2509.minionflow.application;

import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.MyApplication;
import ru.vlad2509.minionflow.application.dto.engine.EngineTaskStatus;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLogsBatch;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.ports.out.TaskEngine;
import ru.vlad2509.minionflow.application.ports.out.TaskPatchHandler;
import ru.vlad2509.minionflow.application.ports.out.TaskPatchNotifier;
import ru.vlad2509.minionflow.application.util.ArtifactService;
import ru.vlad2509.minionflow.application.util.StorageKeyFactory;
import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.TaskRunRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ApplicationScoped
public class TaskAsyncService implements TaskPatchHandler {

    @Inject
    TaskRunRepository taskRunRepository;

    @Inject
    ArtifactService artifactService;

    @Inject
    StorageKeyFactory storageKeyFactory;

    @Named(MyApplication.ENGINE_USED)
    @Inject
    TaskEngine taskEngine;

    @Inject
    TaskPatchNotifier taskPatchNotifier;

    private final ExecutorService executor;
    private static final Logger LOG = LoggerFactory.getLogger(TaskAsyncService.class);

    public TaskAsyncService(@ConfigProperty(name = "artifact-service.task-async-pool-size", defaultValue = "2") int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    // steteless state))
    @Override
    public void onStatelessStatePatch(StatelessTaskState state) {
        EngineTaskStatus newStatus = state.status();
        if (newStatus == null) {
            LOG.error("Missing status in Engine patch for stateless task {}", state.taskId());
            return;
        }
        if (!updateStatus(state.taskId(), newStatus.toTaskStatus()))
            return;

        if (newStatus.toTaskStatus().equals(TaskStatus.FINISHED)) {
            CompletableFuture.runAsync(() -> discoverOutput(state.taskId()), executor).thenAccept((smth) -> {
                StatelessTaskState copy = new StatelessTaskState(state.taskId(), state.seq() + 100, state.kind(), null, TaskStatus.DONE, state.summary(), state.microtasks());
                if (!updateStatus(state.taskId(), TaskStatus.DONE))
                    return;
                taskPatchNotifier.sendStatelessStatePatch(copy);
            });
        }

        StatelessTaskState copy = new StatelessTaskState(state.taskId(), state.seq(), state.kind(), null, newStatus.toTaskStatus(), state.summary(), state.microtasks());
        taskPatchNotifier.sendStatelessStatePatch(copy);
    }

    @Override
    public void onSwarmStatePatch(SwarmTaskState state) {
        EngineTaskStatus newStatus = state.status();
        if (newStatus == null) {
            LOG.error("Missing status in Engine patch for swarm task {}", state.taskId());
            return;
        }
        if (!updateStatus(state.taskId(), newStatus.toTaskStatus()))
            return;

        if (newStatus.toTaskStatus().equals(TaskStatus.FINISHED)) {
            CompletableFuture.runAsync(() -> discoverOutput(state.taskId()), executor).thenAccept((smth) -> {
                SwarmTaskState copy = new SwarmTaskState(state.taskId(), state.seq() + 100, state.kind(), null, TaskStatus.DONE, state.summary(), state.agentStates());
                if (!updateStatus(state.taskId(), TaskStatus.DONE))
                    return;
                taskPatchNotifier.sendSwarmStatePatch(copy);
            });
        }

        SwarmTaskState copy = new SwarmTaskState(state.taskId(), state.seq(), state.kind(), null, newStatus.toTaskStatus(), state.summary(), state.agentStates());
        taskPatchNotifier.sendSwarmStatePatch(copy);
    }

    public void cancelTask(TaskRun taskRun) {
        if (!updateStatus(taskRun.getId(), TaskStatus.CANCELED))
            throw new ApiException(ApiError.TASK_CANCEL_FAIL);
        taskEngine.cancelTask(taskRun);
    }

    @Override
    public void onLogBatch(MicrotaskLogsBatch batch) {
        taskPatchNotifier.sendLogBatch(batch);
    }

    private void discoverOutput(UUID taskId) {
        Optional<TaskRun> taskRunOptional = taskRunRepository.findById(taskId);
        if (taskRunOptional.isEmpty()) {
            LOG.warn("Discover output requested for nonexistent task {}", taskId);
            return;
        }
        TaskRun taskRun = taskRunOptional.get();
        List<Artifact> artifacts = artifactService.discoverArtifact(
                storageKeyFactory.generateOutputsPrefix(taskRun.getProjectId(), taskId),
                taskRun.getUserId(),
                taskRun.getProjectId()
        );
        taskRun.addOutputs(artifacts);
        if (!taskRunRepository.updateOutputsIfEmpty(taskRun)) {
            LOG.warn("Error updating output. Either task was deleted or race condition occured");
        }
    }

    @Transactional
    boolean updateStatus(UUID taskId, TaskStatus status) {
        Optional<TaskRun> taskRunOptional = taskRunRepository.lockById(taskId);
        if (taskRunOptional.isEmpty()) {
            LOG.warn("Update status requested for nonexistent task {}", taskId);
            return false;
        }

        TaskRun taskRun = taskRunOptional.get();
        if (!taskRun.setStatus(status))
            return false;
        taskRunRepository.updateStatus(taskRun);
        return true;
    }

    public void onStartup(@Observes StartupEvent event) {
        taskEngine.registerPatchHandler(this);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}

