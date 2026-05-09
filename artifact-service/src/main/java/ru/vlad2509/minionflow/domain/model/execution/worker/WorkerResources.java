package ru.vlad2509.minionflow.domain.model.execution.worker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkerResources(
        @NotBlank @NotNull String cpu,
        @NotBlank @NotNull String memory) {

}