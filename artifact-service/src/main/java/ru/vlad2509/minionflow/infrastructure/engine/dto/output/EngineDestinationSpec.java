package ru.vlad2509.minionflow.infrastructure.engine.dto.output;

public record EngineDestinationSpec(
        EngineDestinationType type, String bucket, String prefix
) {
}
