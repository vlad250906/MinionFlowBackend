package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.ExecutionConfig;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfigContent;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ExecutionConfigEntity;

import java.time.Instant;
import java.util.UUID;

public record ExecutionConfigDto(
        UUID configId,
        String alias,
        UUID projectId,
        UUID ownerId,
        Instant createdAt,
        ExecutionConfigContent config
) {

    public static ExecutionConfigDto fromDomain(ExecutionConfig entity) {
        return new ExecutionConfigDto(entity.getId(), entity.getAlias(), entity.getProjectId(), entity.getUserId(),
                entity.getCreatedAt(), entity.getContent());
    }

}
