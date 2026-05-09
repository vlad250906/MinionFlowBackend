package ru.vlad2509.minionflow.domain.model.execution.scheduling;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AsapSchedulingSpec(
        @NotNull SchedulingMode mode,
        @PositiveOrZero int maxParallelism,
        @PositiveOrZero int minParallelism) implements SchedulingSpec {
}