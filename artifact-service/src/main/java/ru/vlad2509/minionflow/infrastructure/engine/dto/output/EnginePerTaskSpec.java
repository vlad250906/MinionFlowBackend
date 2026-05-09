package ru.vlad2509.minionflow.infrastructure.engine.dto.output;

public record EnginePerTaskSpec(
        String dirTemplate, EngineResultSpec result
) {
}
