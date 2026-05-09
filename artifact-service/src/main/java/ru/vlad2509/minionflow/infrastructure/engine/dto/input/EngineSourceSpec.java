package ru.vlad2509.minionflow.infrastructure.engine.dto.input;

import java.nio.file.Path;

public record EngineSourceSpec(String bucket, Path key) {
}