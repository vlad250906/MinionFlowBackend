package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfig;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "execution_configs")
public class ExecutionConfigJpa extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(nullable = false)
    public String alias;

    @Column(nullable = false)
    public UUID projectId;

    @Column(nullable = false)
    public UUID userId;

    @Column(nullable = false)
    public Instant createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    public ExecutionConfig content;

    public ExecutionConfigJpa() {
    }

    public ExecutionConfigJpa(String alias, UUID projectId, UUID userId, ExecutionConfig content) {
        this.alias = alias;
        this.projectId = projectId;
        this.userId = userId;
        this.content = content;

        this.createdAt = Instant.now();
        this.id = UUID.randomUUID();
    }
}
