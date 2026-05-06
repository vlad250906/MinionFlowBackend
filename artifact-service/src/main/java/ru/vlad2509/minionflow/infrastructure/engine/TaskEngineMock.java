package ru.vlad2509.minionflow.infrastructure.engine;

import com.google.common.net.MediaType;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.application.dto.engine.BaseTaskSummary;
import ru.vlad2509.minionflow.application.dto.engine.EngineTaskStatus;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLog;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskRunStatus;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskState;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.*;
import ru.vlad2509.minionflow.application.ports.out.S3Service;
import ru.vlad2509.minionflow.application.ports.out.TaskEngine;
import ru.vlad2509.minionflow.application.ports.out.TaskPatchHandler;
import ru.vlad2509.minionflow.application.util.StorageKeyFactory;
import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Named("MockTaskEngine")
@ApplicationScoped
public class TaskEngineMock implements TaskEngine {

    @Inject
    S3Service s3;

    @Inject
    StorageKeyFactory storageKeyFactory;

    private TaskPatchHandler taskPatchHandler;
    private final ExecutorService customExecutor;
    private final Random random = new Random();
    private Set<UUID> cancelledTasks = new HashSet<>();
    private Map<UUID, List<UUID>> microtasks = new HashMap<>();
    private Map<UUID, UUID> taskByMicrotask = new HashMap<>();
    private Object lock;


    public TaskEngineMock(@ConfigProperty(name = "artifact-service.mock-pool-size", defaultValue = "3")
                          int poolSize) {
        this.customExecutor = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public void startTask(TaskRun taskRun) {
        Objects.requireNonNull(taskRun);
        Objects.requireNonNull(taskPatchHandler);

        synchronized (lock) {
            System.out.println("microtasks for task " + taskRun.getId());
            microtasks.put(taskRun.getId(), new ArrayList<>());
            for (int i = 0; i < random.nextInt(1, 6); i++) {
                UUID microtaskId = UUID.randomUUID();
                System.out.println("microtask " + microtaskId);
                microtasks.get(taskRun.getId()).add(microtaskId);
                taskByMicrotask.put(microtaskId, taskRun.getId());
            }
        }

        CompletableFuture.runAsync(() -> {
            try {
                simulateLifecycle(taskRun.getId(), taskRun.getProjectId());
            } catch (InterruptedException ignore) {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, customExecutor);
    }

    @Override
    public void cancelTask(TaskRun taskRun) {
        synchronized (lock) {
            cancelledTasks.add(taskRun.getId());
        }
    }

    // TODO
    @Override
    public Optional<UUID> getTaskByMicrotaskId(UUID microtaskId) {
        synchronized (lock) {
            return taskByMicrotask.containsKey(microtaskId) ? Optional.of(taskByMicrotask.get(microtaskId)) : Optional.empty();
        }
    }

    @Override
    public List<MicrotaskLog> getMicrotaskLogs(UUID microtaskId, int afterSeq, int limit) {
        List<MicrotaskLog> logs = new ArrayList<>();
        if (random.nextInt(5) < 3)
            return logs;
        for (int i = 0; i < random.nextInt(1, 10); i++) {
            logs.add(new MicrotaskLog("INFO", i, Instant.now(), "random log for " + microtaskId + " wioth number" + random.nextInt(1000)));
        }
        return logs;
    }

    @Override
    public StatelessTaskState getStatelessState(TaskRun taskRun) {
        return new StatelessTaskState(taskRun.getId(), 0, "patch", EngineTaskStatus.STARTING, TaskStatus.FAILED,
                new BaseTaskSummary(10, 2, 3, 1, 2, 2, 1),
                microtasks.get(taskRun.getId()).stream().map(microtaskId -> new StatelessMicrotaskState(microtaskId, 0, MicrotaskRunStatus.QUEUED)).toList());
    }

    @Override
    public StatelessMicrotaskRun getStatelessMicrotask(TaskRun taskRun, UUID microtaskId) {
        LocalDateTime now = LocalDateTime.now();
        return new StatelessMicrotaskRun(taskRun.getId(), microtaskId, 0, MicrotaskRunStatus.FAILED, now, now, now, now, 0, "unluck");
    }

    @Override
    public SwarmTaskState getSwarmState(TaskRun taskRun) {
        return new SwarmTaskState(taskRun.getId(), 0, "patch", EngineTaskStatus.STARTING, TaskStatus.FAILED,
                new SwarmTaskSummary(10, 2, 3, 1, 2, 2, 1, 123, SwarmPhase.STEP),
                microtasks.get(taskRun.getId()).stream().map(microtaskId -> new SwarmAgentState(microtaskId, 0, MicrotaskRunStatus.QUEUED, 123, SwarmPhase.STEP)).toList());
    }

    @Override
    public SwarmMicrotaskRun getSwarmMicrotask(TaskRun taskRun, UUID microtaskId) {
        LocalDateTime now = LocalDateTime.now();
        return new SwarmMicrotaskRun(taskRun.getId(), microtaskId, 0, MicrotaskRunStatus.FAILED, now, now, now, now, 0, "unluck", microtaskId, SwarmPhase.STEP, 123);
    }

    @Override
    public SwarmAgent getSwarmAgent(TaskRun taskRun, UUID agentId) {
        LocalDateTime now = LocalDateTime.now();
        return new SwarmAgent(agentId, taskRun.getId(), 0, "inp", "state", SwarmPhase.STEP, 123);
    }

    @Override
    public void registerPatchHandler(TaskPatchHandler handler) {
        taskPatchHandler = handler;
    }

    private void simulateLifecycle(UUID taskId, UUID projectId) throws InterruptedException {
        checkAndUpdate(taskId, EngineTaskStatus.STARTING);
        Thread.sleep(3 * 1000);
        checkAndUpdate(taskId, EngineTaskStatus.RUNNING);
        Thread.sleep(7 * 1000);
        if (random.nextInt(10) < 3) {
            checkAndUpdate(taskId, EngineTaskStatus.FAILED);
            return;
        }
        uploadRandomOutput(taskId, projectId);
        checkAndUpdate(taskId, EngineTaskStatus.SUCCEEDED);
    }

    private void checkAndUpdate(UUID taskId, EngineTaskStatus newStatus) throws InterruptedException {
        boolean cancelled = false;
        synchronized (lock) {
            if (cancelledTasks.contains(taskId)) {
                cancelled = true;
            }
        }
        if (cancelled)
            return;
        taskPatchHandler.onStatelessStatePatch(new StatelessTaskState(taskId, 0, "patch", EngineTaskStatus.STARTING, TaskStatus.FAILED,
                new BaseTaskSummary(10, 2, 3, 1, 2, 2, 1),
                microtasks.get(taskId).stream().map(microtaskId -> new StatelessMicrotaskState(microtaskId, 0, MicrotaskRunStatus.QUEUED)).toList()));
    }

    private boolean uploadRandomOutput(UUID taskId, UUID projectId) {
        try {
            File tmpFile = File.createTempFile(UUID.randomUUID().toString(), ".jsonl");

            FileOutputStream fos = new FileOutputStream(tmpFile);
            fos.write(new String("This is a mock output for task with id = " + taskId).getBytes());
            fos.close();

            String key = storageKeyFactory.generateOutputsPrefix(projectId, taskId) + "/" + "result.jsonl";
            if (!s3.upload(key, tmpFile.toPath(), MediaType.OCTET_STREAM.toString()))
                return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @PreDestroy
    private void shutdown() {
        customExecutor.shutdown();
    }
}
