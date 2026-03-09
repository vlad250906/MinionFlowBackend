package ru.vlad2509.minionflow.domain.model.execution.scheduling;

import jakarta.validation.constraints.PositiveOrZero;

public record FixedSchedulingSpec(
        SchedulingMode mode,
        @PositiveOrZero int parallelism) implements SchedulingSpec {

}