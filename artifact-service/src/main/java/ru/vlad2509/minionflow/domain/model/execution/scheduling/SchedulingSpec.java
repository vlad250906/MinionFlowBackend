package ru.vlad2509.minionflow.domain.model.execution.scheduling;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "mode",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AsapSchedulingSpec.class, name = "asap"),
        @JsonSubTypes.Type(value = FixedSchedulingSpec.class, name = "fixed")
})


public sealed interface SchedulingSpec permits AsapSchedulingSpec, FixedSchedulingSpec {
    SchedulingMode mode();
}
