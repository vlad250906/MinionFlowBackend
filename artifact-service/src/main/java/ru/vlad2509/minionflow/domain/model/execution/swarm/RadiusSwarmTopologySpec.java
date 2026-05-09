package ru.vlad2509.minionflow.domain.model.execution.swarm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RadiusSwarmTopologySpec(
        @NotNull SwarmTopologyTypeDto type,
        @PositiveOrZero Double radius
) implements SwarmTopologySpec{
}