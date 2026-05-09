package ru.vlad2509.minionflow.domain.model.execution.worker;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record WorkerSpec(
        @Valid @NotNull WorkerBound bound,
        @PositiveOrZero int concurrency,
        @Valid @NotNull WorkerResources resources) {

}