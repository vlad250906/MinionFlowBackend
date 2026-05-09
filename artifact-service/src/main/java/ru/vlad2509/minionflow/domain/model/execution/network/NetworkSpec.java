package ru.vlad2509.minionflow.domain.model.execution.network;

import java.util.List;

public record NetworkSpec(
        List<String> allowDomains) {
}