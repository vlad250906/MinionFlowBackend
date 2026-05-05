package ru.vlad2509.minionflow.application.dto.engine.swarm;

import java.util.UUID;

public record SwarmAgent(
        UUID agentId,
        UUID taskId,
        int agentIndex,
        String inputData,
        String stateData,
        SwarmPhase statePhase,
        Integer stateIteration
) {
}