package ru.vlad2509.minionflow.domain.model.execution.swarm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record KNearestSwarmTopologySpec(
        @NotNull SwarmTopologyTypeDto type,
        @PositiveOrZero Integer k
) implements SwarmTopologySpec {
}
