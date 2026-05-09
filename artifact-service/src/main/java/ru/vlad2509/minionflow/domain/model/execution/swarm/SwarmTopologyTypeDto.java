package ru.vlad2509.minionflow.domain.model.execution.swarm;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SwarmTopologyTypeDto {
    @JsonProperty("global")
    GLOBAL,
    @JsonProperty("ring")
    RING,
    @JsonProperty("k-nearest")
    K_NEAREST,
    @JsonProperty("radius")
    RADIUS
}