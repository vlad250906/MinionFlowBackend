package ru.vlad2509.minionflow.domain.model.execution.swarm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SwarmSpec(
        @PositiveOrZero int iterations,
        @PositiveOrZero int agentCount,
        @Valid @NotNull SwarmTopologySpec topology
) {
}
