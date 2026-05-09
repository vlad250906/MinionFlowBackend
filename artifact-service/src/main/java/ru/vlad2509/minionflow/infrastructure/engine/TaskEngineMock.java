package ru.vlad2509.minionflow.infrastructure.engine;

import com.google.common.net.MediaType;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.application.dto.engine.*;
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
import ru.vlad2509.minionflow.domain.model.execution.ExecutionType;

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

    private static final Logger log = LoggerFactory.getLogger(TaskEngineMock.class);
    @Inject
    S3Service s3;

    @Inject
    StorageKeyFactory storageKeyFactory;

    private TaskPatchHandler taskPatchHandler;
    private final Random random = new Random();
    private Object lock = new Object();
    private Map<UUID, BaseTaskState> tasks = new HashMap<>();
    private Map<UUID, StatelessMicrotaskRun> microtasksStateless = new HashMap<>();
    private Map<UUID, SwarmMicrotaskRun> microtasksSwarm = new HashMap<>();
    private Map<UUID, UUID> taskByMicrotask = new HashMap<>();
    private Map<UUID, SwarmAgent> agents = new HashMap<>();
    private Map<UUID, List<MicrotaskLog>> logss = new HashMap<>();
    private Map<UUID, Long> seqs = new HashMap<>();


    @Override
    public void startTask(TaskRun taskRun) {
        Objects.requireNonNull(taskRun);
        Objects.requireNonNull(taskPatchHandler);

        synchronized (lock) {
            if (taskRun.getExecutionConfig().getContent().type() == ExecutionType.STATELESS) {
                List<StatelessMicrotaskState> micro = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    UUID id = UUID.randomUUID();
                    micro.add(new StatelessMicrotaskState(id, i, MicrotaskRunStatus.QUEUED));
                    LocalDateTime now = LocalDateTime.now();
                    microtasksStateless.put(id, new StatelessMicrotaskRun(taskRun.getId(), id, i, MicrotaskRunStatus.QUEUED,
                            now, now, now, now, 0, "nope"));
                    seqs.put(id, 0l);
                    taskByMicrotask.put(id, taskRun.getId());
                    logss.put(id, new ArrayList<>());
                }
                tasks.put(taskRun.getId(), new StatelessTaskState(taskRun.getId(), 0, "snapshot", EngineTaskStatus.STARTING,
                        null, new BaseTaskSummary(10, 10, 0, 0, 0, 0, 0), micro));
            } else {
                List<SwarmAgentState> micro = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    UUID id = UUID.randomUUID();
                    UUID id2 = UUID.randomUUID();
                    LocalDateTime now = LocalDateTime.now();
                    micro.add(new SwarmAgentState(id2, i, MicrotaskRunStatus.QUEUED, 0, SwarmPhase.INIT));
                    agents.put(id2, new SwarmAgent(id2, taskRun.getId(), i, "input", "state", SwarmPhase.INIT, 0));
                    microtasksSwarm.put(id, new SwarmMicrotaskRun(taskRun.getId(), id, i, MicrotaskRunStatus.QUEUED,
                            now, now, now, now, 0, "nope", id2, SwarmPhase.INIT, 0));
                    seqs.put(id, 0l);
                    taskByMicrotask.put(id, taskRun.getId());
                    logss.put(id, new ArrayList<>());
                }
                tasks.put(taskRun.getId(), new SwarmTaskState(taskRun.getId(), 0, "snapshot", EngineTaskStatus.STARTING,
                        null, new SwarmTaskSummary(10, 10, 0, 0, 0, 0, 0, 0, SwarmPhase.INIT), micro));
            }
        }
    }

    @Override
    public void cancelTask(TaskRun taskRun) {
        tasks.remove(taskRun.getId());
    }

    @Override
    public Optional<UUID> getTaskByMicrotaskId(UUID microtaskId) {
        synchronized (lock) {
            var res = taskByMicrotask.getOrDefault(microtaskId, null);
            return res == null ? Optional.empty() : Optional.of(res);
        }
    }

    @Override
    public List<MicrotaskLog> getMicrotaskLogs(UUID microtaskId, int afterSeq, int limit) {
        synchronized (lock) {
            return logss.getOrDefault(microtaskId, null);
        }
    }

    @Override
    public StatelessTaskState getStatelessState(TaskRun taskRun) {
        synchronized (lock) {
            BaseTaskState state = tasks.getOrDefault(taskRun.getId(), null);
            if (state == null)
                return null;
            if (state instanceof SwarmTaskState)
                return null;
            return (StatelessTaskState) state;
        }
    }

    @Override
    public StatelessMicrotaskRun getStatelessMicrotask(TaskRun taskRun, UUID microtaskId) {
        synchronized (lock) {
            return microtasksStateless.getOrDefault(microtaskId, null);
        }
    }

    @Override
    public SwarmTaskState getSwarmState(TaskRun taskRun) {
        synchronized (lock) {
            BaseTaskState state = tasks.getOrDefault(taskRun.getId(), null);
            if (state == null)
                return null;
            if (state instanceof StatelessTaskState)
                return null;
            return (SwarmTaskState) state;
        }
    }

    @Override
    public SwarmMicrotaskRun getSwarmMicrotask(TaskRun taskRun, UUID microtaskId) {
        synchronized (lock) {
            return microtasksSwarm.getOrDefault(microtaskId, null);
        }
    }

    @Override
    public SwarmAgent getSwarmAgent(TaskRun taskRun, UUID agentId) {
        synchronized (lock) {
            return agents.getOrDefault(agentId, null);
        }
    }

    @Override
    public void registerPatchHandler(TaskPatchHandler handler) {
        taskPatchHandler = handler;
    }

    @Scheduled(every = "1s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void simulateLifecycle() {
        synchronized (lock) {
            for (UUID taskId : tasks.keySet()) {
                BaseTaskState state = tasks.get(taskId);
                if (state instanceof StatelessTaskState) {
                    taskPatchHandler.onStatelessStatePatch((StatelessTaskState) state);
                    for (StatelessMicrotaskState micro : ((StatelessTaskState) state).microtasks()) {
                        List<MicrotaskLog> logs = new ArrayList<>();
                        long seq = seqs.get(micro.microtaskId());
                        seqs.put(micro.microtaskId(), seq + 1);
                        logs.add(new MicrotaskLog("INFO", seq, Instant.now(), "Some message number " + seq + " for microtask " + micro.microtaskId()));
                        logss.get(micro.microtaskId()).add(new MicrotaskLog("INFO", seq, Instant.now(), "Some message number " + seq + " for microtask " + micro.microtaskId()));
                        taskPatchHandler.onLogBatch(new MicrotaskLogsBatch(micro.microtaskId(), logs));
                    }
                } else {
                    taskPatchHandler.onSwarmStatePatch((SwarmTaskState) state);
                }
            }
        }
    }
}
