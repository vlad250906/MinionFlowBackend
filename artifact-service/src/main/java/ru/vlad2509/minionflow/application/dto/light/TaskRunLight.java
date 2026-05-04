package ru.vlad2509.minionflow.application.dto.light;

import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;

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

    public static TaskRunLight fromDomain(TaskRun taskRun) {
        return new TaskRunLight(taskRun.getId(), taskRun.getProjectId(), taskRun.getUserId(), taskRun.getStatus(), taskRun.getCreatedAt(), taskRun.getDoneAt());
    }

}
