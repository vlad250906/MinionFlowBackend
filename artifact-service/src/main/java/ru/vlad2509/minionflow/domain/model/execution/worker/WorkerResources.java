package ru.vlad2509.minionflow.domain.model.execution.worker;

import jakarta.validation.constraints.NotBlank;

public record WorkerResources(
        @NotBlank String cpu,
        @NotBlank String memory) {

}