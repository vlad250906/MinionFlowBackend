package ru.vlad2509.minionflow.application.dto.engine;

public record BaseTaskSummary(

        int total,
        int queued,
        int running,
        int succeeded,
        int failed,
        int timedOut,
        double tasksPerSec

) {
}
