package ru.vlad2509.minionflow.domain.model.enums;

public enum TaskStatus {
    CREATED,
    STARTING,
    RUNNING,
    FINISHED,
    TIME_OUT,
    CANCELED,
    FAILED,
    DONE;

    public boolean canChangeTo(TaskStatus next){
        return switch(next){
            case CREATED -> false;
            case STARTING -> this == CREATED;
            case RUNNING -> this == CREATED || this == RUNNING;
            case FINISHED, TIME_OUT -> this == CREATED ||  this == STARTING || this == RUNNING;
            case CANCELED, FAILED -> !isTerminal();
            case DONE -> this == CREATED || this == STARTING || this == RUNNING || this == FINISHED;
        };
    }

    public boolean isTerminal(){
        return this == DONE || this == TIME_OUT || this == CANCELED || this == FAILED;
    }
}
