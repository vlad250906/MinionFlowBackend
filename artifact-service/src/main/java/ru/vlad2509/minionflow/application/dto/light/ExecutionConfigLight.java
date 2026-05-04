package ru.vlad2509.minionflow.application.dto.light;

import ru.vlad2509.minionflow.infrastructure.persistence.model.ExecutionConfigEntity;

import java.time.Instant;
import java.util.UUID;

public record ExecutionConfigLight(
        UUID configId,
        String alias,
        UUID ownerId,
        Instant createdAt
){

    public static ExecutionConfigLight fromJpa(ExecutionConfigEntity entity) {
        return new ExecutionConfigLight(entity.id, entity.alias, entity.userId, entity.createdAt);
    }
}
