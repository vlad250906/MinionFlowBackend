package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.StreamingOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLogsBatch;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.application.dto.TaskRunDto;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmAgent;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.dto.light.TaskRunLight;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.ports.out.TaskEngine;
import ru.vlad2509.minionflow.application.util.ArtifactService;
import ru.vlad2509.minionflow.application.util.StorageKeyFactory;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.domain.model.*;
import ru.vlad2509.minionflow.domain.model.enums.ArtifactType;
import ru.vlad2509.minionflow.domain.model.enums.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.*;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TaskService {

    @Inject
    TokenService tokenService;

    @Inject
    TaskRunRepository taskRunRepository;

    @Inject
    JarArtifactRepository jarArtifactRepository;

    @Inject
    InputArtifactRepository inputArtifactRepository;

    @Inject
    ExecutionConfigRepository executionConfigRepository;

    @Inject
    ArtifactService artifactService;

    @Inject
    @Named("MinionFlowTaskEngine")
    TaskEngine taskEngine;

    @Inject
    StorageKeyFactory storageKeyFactory;

    @Inject
    TaskAsyncService taskAsyncService;

    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);


    public TaskRunDto createTaskRun(UserContext userContext, UUID projectId, UUID jarId, UUID inputId, UUID configId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.TASK_WRITE);
        TaskRun run = createTaskRunTransactional(userContext, projectId, jarId, inputId, configId);
        taskEngine.startTask(run);
        return TaskRunDto.fromDomain(run);
    }

    public void cancelTaskRun(UserContext userContext, UUID projectId, UUID taskRunId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.TASK_WRITE);
        TaskRun taskRun = taskRunRepository.findById(taskRunId).orElseThrow(() -> new ApiException(ApiError.TASK_NOT_FOUND));
        if (!projectId.equals(taskRun.getProjectId()))
            throw new ApiException(ApiError.TASK_NOT_FOUND, "exists, but in different project");
        taskAsyncService.cancelTask(taskRun);
    }

    public TaskRunDto getTaskRun(UserContext userContext, UUID projectId, UUID taskRunId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.TASK_READ);
        TaskRun task = taskRunRepository.findById(taskRunId).orElseThrow(() -> new ApiException(ApiError.TASK_NOT_FOUND));
        if (!projectId.equals(task.getProjectId()))
            throw new ApiException(ApiError.TASK_NOT_FOUND, "exists, but in different project");
        return TaskRunDto.fromDomain(task);
    }

    public List<TaskRunLight> getTaskRuns(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.TASK_READ);
        return taskRunRepository.findAllTasksLight(paginationContext, projectId);
    }

    public MicrotaskLogsBatch getLogs(UserContext userContext, UUID projectId, UUID microtaskId, int afterSeq, int limit) {
        tokenService.authorize(userContext, projectId, ProjectPermission.LOG_READ);
        UUID taskId = taskEngine.getTaskByMicrotaskId(microtaskId)
                .orElseThrow(() -> new ApiException(ApiError.MICROTASK_NOT_FOUND));
        TaskRun task = taskRunRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(ApiError.UNEXPECTED_ERROR, "microtaskId->taskId from engine is invalid"));
        if (!projectId.equals(task.getProjectId()))
            throw new ApiException(ApiError.MICROTASK_NOT_FOUND, "exists, but in different project");
        return new MicrotaskLogsBatch(microtaskId, taskEngine.getMicrotaskLogs(microtaskId, afterSeq, limit));
    }

    public StatelessTaskState getStatelessState(UserContext userContext, UUID projectId, UUID taskId) {
        TaskRun task = taskReadAuth(userContext, projectId, taskId);
        return taskEngine.getStatelessState(task);
    }

    public StatelessMicrotaskRun getStatelessMicrotask(UserContext userContext, UUID projectId, UUID taskId, UUID microtaskId) {
        TaskRun task = taskReadAuth(userContext, projectId, taskId);
        return taskEngine.getStatelessMicrotask(task, microtaskId);
    }

    public SwarmTaskState getSwarmState(UserContext userContext, UUID projectId, UUID taskId) {
        TaskRun task = taskReadAuth(userContext, projectId, taskId);
        return taskEngine.getSwarmState(task);
    }

    public SwarmMicrotaskRun getSwarmMicrotask(UserContext userContext, UUID projectId, UUID taskId, UUID microtaskId) {
        TaskRun task = taskReadAuth(userContext, projectId, taskId);
        return taskEngine.getSwarmMicrotask(task, microtaskId);
    }

    public SwarmAgent getSwarmAgent(UserContext userContext, UUID projectId, UUID taskId, UUID agentId) {
        TaskRun task = taskReadAuth(userContext, projectId, taskId);
        return taskEngine.getSwarmAgent(task, agentId);
    }

    @Transactional
    public List<ArtifactDto> getOutputs(UserContext userContext, UUID projectId, UUID taskId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.OUTPUT_READ);
        TaskRun taskRun = taskRunRepository.findById(taskId).orElseThrow(() -> new ApiException(ApiError.TASK_NOT_FOUND));
        if (!projectId.equals(taskRun.getProjectId()))
            throw new ApiException(ApiError.TASK_NOT_FOUND, "exists, but in different project");
        return taskRun.getOutputs().stream().map(ArtifactDto::fromDomain).toList();
    }

    @Transactional
    public ArtifactDto getOutputMetadata(UserContext userContext, UUID projectId, UUID taskRunId, UUID outputId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.OUTPUT_READ);
        Artifact artifact = artifactService.getArtifactMetadata(userContext, outputId);
        if (!projectId.equals(artifact.getProjectId()))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "exists, but in different project");
        if (artifact.getType() != ArtifactType.OUTPUT)
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "why are you trying to get " + artifact.getType() + " instead of OUTPUT type from here??");
        return ArtifactDto.fromDomain(artifact);
    }

    @Transactional
    public StreamingOutput getOutputContent(UserContext userContext, UUID projectId, UUID taskRunId, UUID outputId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.OUTPUT_READ);
        return artifactService.downloadArtifact(userContext, projectId, outputId);
    }


    @Transactional
    TaskRun createTaskRunTransactional(UserContext userContext, UUID projectId, UUID jarId, UUID inputId, UUID configId) {
        JarArtifact jar = jarArtifactRepository.findByArtifactId(jarId).orElseThrow(() -> new ApiException(ApiError.JAR_NOT_FOUND));
        InputArtifact input = inputArtifactRepository.findByArtifactId(inputId).orElseThrow(() -> new ApiException(ApiError.INPUT_NOT_FOUND));
        ExecutionConfig executionConfig = executionConfigRepository.findById(configId).orElseThrow(() -> new ApiException(ApiError.EXECUTION_CONFIG_NOT_FOUND));

        TaskRun taskRun = new TaskRun(projectId, userContext.userId(), jar.getStorageIdentifier(), input.getStorageIdentifier(), jar, input, executionConfig);
        taskRunRepository.create(taskRun);
        return taskRun;
    }

    @Transactional
    TaskRun taskReadAuth(UserContext userContext, UUID projectId, UUID taskId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.TASK_READ);
        TaskRun task = taskRunRepository.findById(taskId).orElseThrow(() -> new ApiException(ApiError.TASK_NOT_FOUND));
        if (!projectId.equals(task.getProjectId()))
            throw new ApiException(ApiError.TASK_NOT_FOUND, "exists, but in different project");
        return task;
    }


}
