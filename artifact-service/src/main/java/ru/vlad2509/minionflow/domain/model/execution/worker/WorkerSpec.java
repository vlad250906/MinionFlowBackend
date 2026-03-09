package ru.vlad2509.minionflow.domain.model.execution.worker;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

public record WorkerSpec(
        @Valid WorkerBound bound,
        @PositiveOrZero int concurrency,
        @Valid WorkerResources resources) {

}