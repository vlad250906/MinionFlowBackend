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
import ru.vlad2509.minionflow.domain.model.ProjectPermission;
import ru.vlad2509.minionflow.domain.model.TaskStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.model.*;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.*;
import ru.vlad2509.minionflow.infrastructure.s3.S3ServiceImplementation;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
        //TODO: проверять, что projectId соответствует projectId объекта (во всех сервисах)!!!
        tokenService.authorize(userContext, projectId, ProjectPermission.TASK_WRITE);
        TaskRun run = createTaskRunTransactional(userContext, projectId, jarId, inputId, configId);
        taskEngine.startTask(run);
        return TaskRunDto.fromJpa(run);
    }

    public void cancelTaskRun(UserContext userContext, UUID projectId, UUID taskRunId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.TASK_WRITE);
        taskEngine.cancelTask(taskRunId);
    }

    public TaskRunDto getTaskRun(UserContext userContext, UUID projectId, UUID taskRunId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.TASK_READ);
        TaskRun task = taskRunRepository.findById(taskRunId).orElseThrow(() -> new ApiException(ApiError.TASK_NOT_FOUND));
        return TaskRunDto.fromJpa(task);
    }

    public List<TaskRunLight> getTaskRuns(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.TASK_READ);
        return taskRunRepository.findAllTasks(paginationContext, projectId).stream().map(TaskRunLight::fromJpa).toList();
    }

    @Transactional
    public List<ArtifactDto> getOutputs(UserContext userContext, UUID projectId, UUID taskId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.OUTPUT_READ);
        TaskRun taskRun = taskRunRepository.findById(taskId).orElseThrow(() -> new ApiException(ApiError.TASK_NOT_FOUND));
        return taskRun.outputs.stream().map(ArtifactDto::fromJpa).toList();
    }

    @Transactional
    public ArtifactDto getOutputMetadata(UserContext userContext, UUID projectId, UUID taskRunId, UUID outputId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.OUTPUT_READ);
        return artifactService.getArtifactMetadata(userContext, outputId);
    }

    @Transactional
    public StreamingOutput getOutputContent(UserContext userContext, UUID projectId, UUID taskRunId, UUID outputId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.OUTPUT_READ);
        return artifactService.downloadArtifact(userContext, outputId);
    }

    public void onStatusChange(UUID taskId, TaskStatus newStatus) {
        if (newStatus == TaskStatus.DONE)
            discoverOutput(taskId);

        updateStatus(taskId, newStatus);
    }

    void discoverOutput(UUID taskId) {
        Optional<ArtifactContext> artifactContextOptional = loadArtifactContext(taskId);
        if (artifactContextOptional.isPresent()) {
            ArtifactContext ctx = artifactContextOptional.get();
            List<UUID> artifactIds = artifactService.discoverArtifact(
                    storageKeyFactory.generateOutputsPrefix(ctx.projectId(), taskId),
                    ctx.userId(),
                    ctx.projectId()
            ).stream().map(ArtifactDto::artifactId).toList();
            setTaskOutput(taskId, artifactIds);
        }
    }

    @Transactional
    void setTaskOutput(UUID taskId, List<UUID> artifactId) {
        taskRunRepository.updateOutputs(taskId, artifactId);
    }

    @Transactional
    Optional<ArtifactContext> loadArtifactContext(UUID taskId) {
        return taskRunRepository.findById(taskId)
                .map(task -> new ArtifactContext(task.userId, task.projectId));
    }

    @Transactional
    void updateStatus(UUID taskId, TaskStatus newStatus) {
        TaskRun task = taskRunRepository.findById(taskId).orElse(null);
        if (task == null) {
            LOG.warn("Task with id {} does not exists, can't update its status!", taskId);
            return;
        }

        // TODO: проверка доменных переходов статусов
        task.status = newStatus;
    }

    record ArtifactContext(UUID userId, UUID projectId) {
    }

    @Transactional
    TaskRun createTaskRunTransactional(UserContext userContext, UUID projectId, UUID jarId, UUID inputId, UUID configId) {
        JarArtifact jar = jarArtifactRepository.findByArtifactId(jarId).orElseThrow(() -> new ApiException(ApiError.JAR_NOT_FOUND));
        InputArtifact input = inputArtifactRepository.findByArtifactId(inputId).orElseThrow(() -> new ApiException(ApiError.INPUT_NOT_FOUND));
        ExecutionConfigJpa executionConfig = executionConfigRepository.findById(configId).orElseThrow(() -> new ApiException(ApiError.EXECUTION_CONFIG_NOT_FOUND));

        TaskRun taskRun = new TaskRun(projectId, userContext.userId(), jar.artifact.storageIdentifier, input.artifact.storageIdentifier, jar, input, executionConfig);
        taskRunRepository.persist(taskRun);
        return taskRun;
    }

    @PostConstruct
    public void postConstruct() {
        taskEngine.registerStatusHandler(this::onStatusChange);
    }

}
