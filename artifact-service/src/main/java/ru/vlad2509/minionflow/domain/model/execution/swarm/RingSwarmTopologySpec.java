package ru.vlad2509.minionflow.domain.model.execution.swarm;

import jakarta.validation.constraints.PositiveOrZero;

public record RingSwarmTopologySpec(
        SwarmTopologyTypeDto type,
        @PositiveOrZero Integer numberOfNeighbors
) implements SwarmTopologySpec {
}