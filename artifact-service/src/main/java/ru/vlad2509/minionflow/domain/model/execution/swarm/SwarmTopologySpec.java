package ru.vlad2509.minionflow.domain.model.execution.swarm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = GlobalSwarmTopologySpec.class, name = "global"),
        @JsonSubTypes.Type(value = RingSwarmTopologySpec.class, name = "ring"),
        @JsonSubTypes.Type(value = KNearestSwarmTopologySpec.class, name = "k-nearest"),
        @JsonSubTypes.Type(value = RadiusSwarmTopologySpec.class, name = "radius")
})

public sealed interface SwarmTopologySpec permits GlobalSwarmTopologySpec, KNearestSwarmTopologySpec, RadiusSwarmTopologySpec, RingSwarmTopologySpec {
    SwarmTopologyTypeDto type();
}
