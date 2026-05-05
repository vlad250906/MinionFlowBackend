package ru.vlad2509.minionflow.infrastructure.engine;

import com.google.common.net.MediaType;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.application.dto.engine.EngineTaskStatus;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLog;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmAgent;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.ports.out.S3Service;
import ru.vlad2509.minionflow.application.ports.out.TaskEngine;
import ru.vlad2509.minionflow.application.ports.out.TaskPatchHandler;
import ru.vlad2509.minionflow.application.util.StorageKeyFactory;
import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private Object lock = new Object();

    public TaskEngineMock(@ConfigProperty(name = "artifact-service.mock-pool-size", defaultValue = "3")
                          int poolSize) {
        this.customExecutor = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public void startTask(TaskRun taskRun) {
        Objects.requireNonNull(taskRun);
        Objects.requireNonNull(taskPatchHandler);

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

    @Override
    public List<MicrotaskLog> getMicrotaskLogs(UUID microtaskId, int afterSeq, int limit) {
        return List.of();
    }

    @Override
    public StatelessTaskState getStatelessState(TaskRun taskRun) {
        return null;
    }

    @Override
    public StatelessMicrotaskRun getStatelessMicrotask(TaskRun taskRun, UUID microtaskId) {
        return null;
    }

    @Override
    public SwarmTaskState getSwarmState(TaskRun taskRun) {
        return null;
    }

    @Override
    public SwarmMicrotaskRun getSwarmMicrotask(TaskRun taskRun, UUID microtaskId) {
        return null;
    }

    @Override
    public SwarmAgent getSwarmAgent(TaskRun taskRun, UUID agentId) {
        return null;
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
        checkAndUpdate(taskId, EngineTaskStatus.SUCCEEDED);
        Thread.sleep(2 * 1000);

        checkAndUpdate(taskId, uploadRandomOutput(taskId, projectId) ? EngineTaskStatus.SUCCEEDED : EngineTaskStatus.SUCCEEDED);
    }

    private void checkAndUpdate(UUID taskId, EngineTaskStatus newStatus) throws InterruptedException {
        boolean cancelled = false;
        synchronized (lock) {
            if (cancelledTasks.contains(taskId)) {
                cancelled = true;
            }
        }
//        if (cancelled) {
//            taskPatchHandler.updateTaskStatus(taskId, TaskStatus.CANCELED);
//            throw new InterruptedException();
//        }
        taskPatchHandler.updateTaskStatus(taskId, newStatus);
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
