package ru.vlad2509.minionflow.domain.model.execution.retry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import ru.vlad2509.minionflow.domain.exception.ExecutionConfigException;

public record BackoffSpec(
        @NotBlank String strategy,
        @PositiveOrZero int baseMs,
        @PositiveOrZero int maxMs,
        boolean jitter) {

}
