package ru.vlad2509.minionflow.infrastructure.engine.dto.output;

public record EngineOutputSpec(
        EngineDestinationSpec destination,
        EnginePerTaskSpec perTask,
        EngineArtifactsSpec artifacts
) {
}
