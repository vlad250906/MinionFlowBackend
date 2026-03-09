package ru.vlad2509.minionflow.application.dto.light;

import ru.vlad2509.minionflow.domain.model.TaskStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.model.TaskRun;

import java.time.Instant;
import java.util.UUID;

public record TaskRunLight(

        UUID taskId,
        UUID projectId,
        UUID launchedByUser,
        TaskStatus status,
        Instant createdAt,
        Instant doneAt
) {

    public static TaskRunLight fromJpa(TaskRun jpa) {
        return new TaskRunLight(jpa.id, jpa.projectId, jpa.userId, jpa.status, jpa.createdAt, jpa.doneAt);
    }

}
