package ru.vlad2509.minionflow.domain.model.execution;

import jakarta.validation.constraints.PositiveOrZero;

public record TimeoutsSpec(
        @PositiveOrZero long microtaskSeconds,
        @PositiveOrZero long taskSeconds) {

}
