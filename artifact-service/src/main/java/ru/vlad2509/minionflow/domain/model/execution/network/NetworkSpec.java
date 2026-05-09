package ru.vlad2509.minionflow.domain.model.execution.network;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NetworkSpec(
        @NotNull List<String> allowDomains) {
}