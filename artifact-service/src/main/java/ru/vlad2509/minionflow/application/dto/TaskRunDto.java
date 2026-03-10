package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.TaskStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.model.TaskRun;

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

    public static TaskRunDto fromJpa(TaskRun jpa) {
        return new TaskRunDto(jpa.id, jpa.projectId, jpa.userId, jpa.status,
                jpa.jarJpa == null ? null : jpa.jarJpa.artifact.id, jpa.jarJpa == null ? null : jpa.jarJpa.alias,
                jpa.inputJpa == null ? null : jpa.inputJpa.artifact.id, jpa.inputJpa == null ? null : jpa.inputJpa.alias,
                jpa.executionConfig == null ? null : jpa.executionConfig.id, jpa.executionConfig == null ? null : jpa.executionConfig.alias,
                jpa.createdAt, jpa.startedAt, jpa.finishedAt, jpa.doneAt);
    }

}
