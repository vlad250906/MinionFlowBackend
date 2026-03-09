package ru.vlad2509.minionflow.domain.model.execution.retry;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

public record RetrySpec(
        @PositiveOrZero int maxAttempts,
        @Valid BackoffSpec backoff) {
}
