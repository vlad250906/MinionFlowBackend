package ru.vlad2509.minionflow.application.ports.out;

import ru.vlad2509.minionflow.domain.model.TaskRun;

public interface TaskEngine {

    void startTask(TaskRun taskRun);

    void cancelTask(TaskRun taskRun);

    void registerStatusHandler(TaskStatusHandler handler);

}
