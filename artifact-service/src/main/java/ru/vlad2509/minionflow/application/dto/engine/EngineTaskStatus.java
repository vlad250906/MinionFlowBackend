package ru.vlad2509.minionflow.application.dto.engine;

import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;

public enum EngineTaskStatus {
    STARTING,
    RUNNING,
    SUCCEEDED,
    FAILED,
    TIMED_OUT;

    public TaskStatus toTaskStatus() {
        return switch (this){
            case STARTING -> TaskStatus.STARTING;
            case RUNNING -> TaskStatus.RUNNING;
            case SUCCEEDED -> TaskStatus.FINISHED;
            case FAILED -> TaskStatus.FAILED;
            case TIMED_OUT -> TaskStatus.TIME_OUT;
        };
    }
}
