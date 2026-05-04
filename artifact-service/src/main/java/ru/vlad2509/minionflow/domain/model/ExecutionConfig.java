package ru.vlad2509.minionflow.domain.model;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfigContent;

import java.time.Instant;
import java.util.UUID;

public class ExecutionConfig {

    private UUID id;
    private String alias;
    private UUID projectId;
    private UUID userId;
    private Instant createdAt;
    private ExecutionConfigContent content;

    public ExecutionConfig(UUID id, String alias, UUID projectId, UUID userId, Instant createdAt, ExecutionConfigContent content) {
        this.id = id;
        this.alias = alias;
        this.projectId = projectId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.content = content;
    }

    public ExecutionConfig(String alias, UUID projectId, UUID userId, ExecutionConfigContent content) {
        this.alias = alias;
        this.projectId = projectId;
        this.userId = userId;
        this.content = content;

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ExecutionConfigContent getContent() {
        return content;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setContent(ExecutionConfigContent content) {
        this.content = content;
    }
}
