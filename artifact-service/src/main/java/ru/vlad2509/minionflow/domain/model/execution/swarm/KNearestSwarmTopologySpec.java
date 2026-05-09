package ru.vlad2509.minionflow.domain.model.execution.swarm;

import jakarta.validation.constraints.PositiveOrZero;

public record KNearestSwarmTopologySpec(
        SwarmTopologyTypeDto type,
        @PositiveOrZero Integer k
) implements SwarmTopologySpec {
}
