package ru.vlad2509.minionflow.domain.model.execution.swarm;

import jakarta.validation.constraints.PositiveOrZero;

public record RadiusSwarmTopologySpec(
        SwarmTopologyTypeDto type,
        @PositiveOrZero Double radius
) implements SwarmTopologySpec{
}