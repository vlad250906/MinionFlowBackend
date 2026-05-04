package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public record TaskRunDto(
        UUID taskId,
        UUID projectId,
        UUID launchedByUser,
        TaskStatus status,
        UUID jarId,
        String jarAlias,
        UUID inputId,
        String inputAlias,
        UUID configId,
        String configAlias,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        Instant doneAt

) {

    public static TaskRunDto fromDomain(TaskRun taskRun) {
        // :/
        return new TaskRunDto(taskRun.getId(), taskRun.getProjectId(), taskRun.getUserId(), taskRun.getStatus(),
                taskRun.getJarArtifact() == null ? null : taskRun.getJarArtifact().getId(),
                taskRun.getJarArtifact() == null ? null : taskRun.getJarArtifact().getAlias(),
                taskRun.getInputArtifact() == null ? null : taskRun.getInputArtifact().getId(),
                taskRun.getInputArtifact() == null ? null : taskRun.getInputArtifact().getAlias(),
                taskRun.getExecutionConfig() == null ? null : taskRun.getExecutionConfig().getId(),
                taskRun.getExecutionConfig() == null ? null : taskRun.getExecutionConfig().getAlias(),
                taskRun.getCreatedAt(), taskRun.getStartedAt(), taskRun.getFinishedAt(), taskRun.getDoneAt()
                );
    }

}
