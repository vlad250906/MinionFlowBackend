package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.vlad2509.minionflow.domain.model.ExecutionConfig;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfigContent;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "execution_configs")
public class ExecutionConfigEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "alias", nullable = false)
    public String alias;

    @Column(name = "project_id", nullable = false)
    public UUID projectId;

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
    public Instant createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", nullable = false)
    public ExecutionConfigContent content;

    public ExecutionConfigEntity() {
    }

    public ExecutionConfigEntity(UUID id, String alias, UUID projectId, UUID userId, Instant createdAt, ExecutionConfigContent content) {
        this.id = id;
        this.alias = alias;
        this.projectId = projectId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.content = content;
    }

    public ExecutionConfig toDomain() {
        return new ExecutionConfig(id, alias, projectId, userId, createdAt, content);
    }

    public static ExecutionConfigEntity fromDomain(ExecutionConfig config) {
        return new ExecutionConfigEntity(config.getId(), config.getAlias(), config.getProjectId(), config.getUserId(),
                config.getCreatedAt(), config.getContent());
    }
}
