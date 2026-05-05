package ru.vlad2509.minionflow.application.dto.engine.swarm;

public record SwarmTaskSummary (
        int total,
        int queued,
        int running,
        int succeeded,
        int failed,
        int timedOut,
        double tasksPerSec,
        int currentIteration,
        SwarmPhase currentPhase
) {
}
