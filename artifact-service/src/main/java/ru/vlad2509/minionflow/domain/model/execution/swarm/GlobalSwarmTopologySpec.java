package ru.vlad2509.minionflow.domain.model.execution.swarm;

import jakarta.validation.constraints.NotNull;

public record GlobalSwarmTopologySpec(
        @NotNull SwarmTopologyTypeDto type
) implements SwarmTopologySpec {
}