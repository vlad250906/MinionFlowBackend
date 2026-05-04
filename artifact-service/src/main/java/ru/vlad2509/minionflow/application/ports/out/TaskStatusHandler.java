package ru.vlad2509.minionflow.application.ports.out;

import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;

import java.util.UUID;

public interface TaskStatusHandler {

    void updateTaskStatus(UUID taskId, TaskStatus newTaskStatus);
}
