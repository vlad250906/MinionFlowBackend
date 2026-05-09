package ru.vlad2509.minionflow.infrastructure.engine.dto;

import java.util.UUID;

public record EngineCreateTaskRunRequest(
        UUID projectId,
        UUID taskId,
        String jarFileName,
        EngineTaskConfiguration configuration
) {
}
