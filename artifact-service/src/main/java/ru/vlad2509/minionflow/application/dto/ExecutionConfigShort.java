package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfig;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ExecutionConfigJpa;

import java.time.Instant;
import java.util.UUID;

public record ExecutionConfigShort (
        UUID configId,
        String alias,
        UUID ownerId,
        Instant createdAt
){

    public static ExecutionConfigShort fromJpa(ExecutionConfigJpa entity) {
        return new ExecutionConfigShort(entity.id, entity.alias, entity.userId, entity.createdAt);
    }
}
