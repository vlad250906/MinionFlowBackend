package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfig;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ExecutionConfigJpa;

import java.time.Instant;
import java.util.UUID;

public record ExecutionConfigDto(
        UUID configId,
        String alias,
        UUID projectId,
        UUID ownerId,
        Instant createdAt,
        ExecutionConfig config
) {

    public static ExecutionConfigDto fromJpa(ExecutionConfigJpa entity) {
        return new ExecutionConfigDto(entity.id, entity.alias, entity.projectId, entity.userId, entity.createdAt, entity.content);
    }

}
