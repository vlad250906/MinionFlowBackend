package ru.vlad2509.minionflow.application.dto.engine.swarm;

import ru.vlad2509.minionflow.application.dto.engine.MicrotaskRunStatus;

import java.util.UUID;

public record SwarmAgentState (
        UUID agentId,
        int displayIndex,
        MicrotaskRunStatus status,
        int currentIteration,
        SwarmPhase currentPhase
) {
}
