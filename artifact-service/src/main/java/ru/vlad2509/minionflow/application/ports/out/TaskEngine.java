package ru.vlad2509.minionflow.application.ports.out;

import ru.vlad2509.minionflow.infrastructure.persistence.model.TaskRun;

import java.util.UUID;

public interface TaskEngine {

    void startTask(TaskRun taskRun);

    void cancelTask(UUID taskId);

    void registerStatusHandler(TaskStatusHandler handler);

}
