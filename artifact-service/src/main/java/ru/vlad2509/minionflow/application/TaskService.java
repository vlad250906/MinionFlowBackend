package ru.vlad2509.minionflow.application;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.StreamingOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.application.dto.TaskRunDto;
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
import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.*;

import java.util.List;
import java.util.Optional;
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
    TaskEngine taskEngine;

    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);
    @Inject
    StorageKeyFactory storageKeyFactory;


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
        taskEngine.cancelTask(taskRun);
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

    public void onStatusChange(UUID taskId, TaskStatus newStatus) {
        if (newStatus == TaskStatus.DONE)
            discoverOutput(taskId);

        updateStatus(taskId, newStatus);
    }

    void discoverOutput(UUID taskId) {
        Optional<TaskRun> taskRunOptional = taskRunRepository.findById(taskId);
        if (taskRunOptional.isPresent()) {
            TaskRun taskRun = taskRunOptional.get();
            List<Artifact> artifactIds = artifactService.discoverArtifact(
                    storageKeyFactory.generateOutputsPrefix(taskRun.getProjectId(), taskId),
                    taskRun.getUserId(),
                    taskRun.getProjectId()
            );
            setTaskOutput(taskRun, artifactIds);
        }
    }

    @Transactional
    void setTaskOutput(TaskRun taskRun, List<Artifact> artifactIds) {
        taskRun.addOutputs(artifactIds);
        taskRunRepository.updateOutputs(taskRun);
    }

    @Transactional
    void updateStatus(UUID taskId, TaskStatus newStatus) {
        TaskRun task = taskRunRepository.findById(taskId).orElse(null);
        if (task == null) {
            LOG.warn("Task with id {} does not exists, can't update its status!", taskId);
            return;
        }

        // TODO: проверка доменных переходов статусов
        // TODO: Сделать логику с FOR UPDATE у тасков
        // TODO: флажок "выходные данные готовы"
        task.setStatus(newStatus);

    }

    record ArtifactContext(UUID userId, UUID projectId) {
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

    @PostConstruct
    public void postConstruct() {
        taskEngine.registerStatusHandler(this::onStatusChange);
    }

}
