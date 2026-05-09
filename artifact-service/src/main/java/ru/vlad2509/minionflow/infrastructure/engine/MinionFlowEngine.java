package ru.vlad2509.minionflow.infrastructure.engine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLog;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskState;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmAgent;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.ports.out.TaskEngine;
import ru.vlad2509.minionflow.application.ports.out.TaskPatchHandler;
import ru.vlad2509.minionflow.application.util.StorageKeyFactory;
import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.infrastructure.engine.dto.EngineCreateTaskRunRequest;
import ru.vlad2509.minionflow.infrastructure.engine.dto.EngineExecutionSpec;
import ru.vlad2509.minionflow.infrastructure.engine.dto.EngineSecuritySpec;
import ru.vlad2509.minionflow.infrastructure.engine.dto.EngineTaskConfiguration;
import ru.vlad2509.minionflow.infrastructure.engine.dto.input.EngineInputSpec;
import ru.vlad2509.minionflow.infrastructure.engine.dto.input.EngineInputType;
import ru.vlad2509.minionflow.infrastructure.engine.dto.input.EngineSourceSpec;
import ru.vlad2509.minionflow.infrastructure.engine.dto.output.*;
import ru.vlad2509.minionflow.infrastructure.engine.rest.EngineApiRestClient;
import ru.vlad2509.minionflow.infrastructure.engine.rest.EngineLogRestClient;
import ru.vlad2509.minionflow.infrastructure.messaging.events.LogBatchEventListener;
import ru.vlad2509.minionflow.infrastructure.messaging.events.TaskStateEventListener;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Named("MinionFlowTaskEngine")
@ApplicationScoped
public class MinionFlowEngine implements TaskEngine {

    @ConfigProperty(name = "artifact-service.bucket_name", defaultValue = "minionflow")
    String bucketName;

    @Inject
    StorageKeyFactory storageKeyFactory;

    @RestClient
    @Inject
    EngineApiRestClient engineApiRestClient;

    @RestClient
    @Inject
    EngineLogRestClient engineLogRestClient;

    @Inject
    LogBatchEventListener logBatchEventListener;

    @Inject
    TaskStateEventListener taskStateEventListener;

    private TaskPatchHandler taskPatchHandler;
    private static final Logger LOG = LoggerFactory.getLogger(MinionFlowEngine.class);
    private final Cache<UUID, UUID> microtaskToTask = Caffeine.newBuilder()
            .maximumSize(50000)
            .expireAfterAccess(Duration.ofMinutes(30))
            .build();

    @PostConstruct
    public void init() {
        logBatchEventListener.setEventHandler(taskPatchHandler::onLogBatch);
        taskStateEventListener.setEventHandler(baseTaskState -> {
            switch (baseTaskState) {
                case StatelessTaskState sts -> {
                    for (StatelessMicrotaskState micro : sts.microtasks()) {
                        microtaskToTask.put(micro.microtaskId(), sts.taskId());
                    }
                    taskPatchHandler.onStatelessStatePatch(sts);
                }
                case SwarmTaskState sts -> {
                    // FIXME
                    // А тут микротаски не получить вообще никак
                    taskPatchHandler.onSwarmStatePatch(sts);
                }
                default -> LOG.warn("Received unknown subclass of BaseTaskState: {}, skipping it", baseTaskState);
            }
        });
    }

    @Override
    public void startTask(TaskRun taskRun) {
        String inputArtifactName = storageKeyFactory.extractLastFromPath(taskRun.getInputArtifactIdentifier().getStorageKey());
        String jarArtifactName = storageKeyFactory.extractLastFromPath(taskRun.getJarArtifactIdentifier().getStorageKey());

        EngineExecutionSpec executionSpec = EngineExecutionSpec.fromDomain(taskRun.getExecutionConfig().getContent());
        EngineInputSpec inputSpec = new EngineInputSpec(
                EngineInputType.fromDomain(taskRun.getInputArtifact().getInputType()),
                new EngineSourceSpec(bucketName, Path.of(inputArtifactName)));
        EngineOutputSpec outputSpec = new EngineOutputSpec(
                new EngineDestinationSpec(EngineDestinationType.S3, bucketName, "currently-unused"),
                new EnginePerTaskSpec("currently-unused", new EngineResultSpec(EngineResultFormat.JSON, "currently-unused")),
                new EngineArtifactsSpec("currently-unused", "currently-unused"));
        EngineSecuritySpec securitySpec = new EngineSecuritySpec(taskRun.getExecutionConfig().getContent().network());

        EngineCreateTaskRunRequest request = new EngineCreateTaskRunRequest(taskRun.getProjectId(), taskRun.getId(), jarArtifactName,
                new EngineTaskConfiguration(executionSpec, inputSpec, outputSpec, securitySpec));

        try {
            engineApiRestClient.runTask(request);
        } catch (ApiException ex) {
            LOG.error("Failed to start task in engine", ex);
            throw new ApiException(ApiError.ENGINE_REQUEST_FAILED);
        } catch (Exception ex) {
            LOG.error("Engine not available", ex);
            throw new ApiException(ApiError.ENGINE_UNAVAILABLE);
        }
    }

    @Override
    public void cancelTask(TaskRun taskRun) {
        // not supported
    }

    @Override
    public Optional<UUID> getTaskByMicrotaskId(UUID microtaskId) {
        UUID taskId = microtaskToTask.get(microtaskId, this::getFromEngine);
        return taskId == null ? Optional.empty() : Optional.of(taskId);
    }

    private UUID getFromEngine(UUID microtaskId) {
        // FIXME
        // Да, серьёзно. API в движке для получения taskId по microtaskId просто нету)))
        return null;
    }

    @Override
    public List<MicrotaskLog> getMicrotaskLogs(UUID microtaskId, int afterSeq, int limit) {
        try {
            return engineLogRestClient.getLogs(microtaskId, afterSeq, limit).logs();
        } catch (ApiException ex) {
            LOG.error("Engine failed to process the request", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("Engine not available", ex);
            throw new ApiException(ApiError.ENGINE_UNAVAILABLE);
        }
    }

    @Override
    public StatelessTaskState getStatelessState(TaskRun taskRun) {
        try {
            return engineApiRestClient.getStatelessState(taskRun.getId());
        } catch (ApiException ex) {
            LOG.error("Engine failed to process the request", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("Engine not available", ex);
            throw new ApiException(ApiError.ENGINE_UNAVAILABLE);
        }
    }

    @Override
    public StatelessMicrotaskRun getStatelessMicrotask(TaskRun taskRun, UUID microtaskId) {
        try {
            return engineApiRestClient.getStatelessMicrotask(taskRun.getId(), microtaskId);
        } catch (ApiException ex) {
            LOG.error("Engine failed to process the request", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("Engine not available", ex);
            throw new ApiException(ApiError.ENGINE_UNAVAILABLE);
        }
    }

    @Override
    public SwarmTaskState getSwarmState(TaskRun taskRun) {
        try {
            return engineApiRestClient.getSwarmState(taskRun.getId());
        } catch (ApiException ex) {
            LOG.error("Engine failed to process the request", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("Engine not available", ex);
            throw new ApiException(ApiError.ENGINE_UNAVAILABLE);
        }
    }

    @Override
    public SwarmMicrotaskRun getSwarmMicrotask(TaskRun taskRun, UUID microtaskId) {
        try {
            return engineApiRestClient.getSwarmMicrotask(taskRun.getId(), microtaskId);
        } catch (ApiException ex) {
            LOG.error("Engine failed to process the request", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("Engine not available", ex);
            throw new ApiException(ApiError.ENGINE_UNAVAILABLE);
        }
    }

    @Override
    public SwarmAgent getSwarmAgent(TaskRun taskRun, UUID agentId) {
        try {
            return engineApiRestClient.getSwarmAgent(taskRun.getId(), agentId);
        } catch (ApiException ex) {
            LOG.error("Engine failed to process the request", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("Engine not available", ex);
            throw new ApiException(ApiError.ENGINE_UNAVAILABLE);
        }
    }

    @Override
    public void registerPatchHandler(TaskPatchHandler handler) {
        this.taskPatchHandler = handler;
    }
}
