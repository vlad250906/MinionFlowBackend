package ru.vlad2509.minionflow.infrastructure.engine.dto;

import ru.vlad2509.minionflow.domain.model.execution.network.NetworkSpec;

public record EngineSecuritySpec(NetworkSpec network) {
}
