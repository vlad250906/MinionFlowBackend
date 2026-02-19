package ru.vlad2509.minionflow.infrastructure.persistence.model;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import ru.vlad2509.minionflow.domain.ArtifactType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "artifacts")
public class Artifact extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(nullable = false)
    public UUID projectId;

    @Column(nullable = false)
    public UUID userId;

    @Column(nullable = false)
    public boolean markDeleted;

    @Column(nullable = false)
    public long size;

    @Column(nullable = false)
    public String alias;

    @Column(nullable = false)
    public String originalName;

    @Column(nullable = false)
    public String contentType;

    @Column(nullable = false)
    public String hashAlgorithm;

    @Column(nullable = false)
    public String hashValue;

    @Column(nullable = false)
    public Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public ArtifactType type;

    @Column(nullable = false)
    public String storageKey;

    public Artifact() {
    }

    public Artifact(UUID projectId, UUID userId, ArtifactType type, String alias, long size, String originalName, String contentType,
                    String hashAlgorithm, String hashValue, String storageKey) {
        this.projectId = projectId;
        this.userId = userId;
        this.type = type;
        this.alias = alias;
        this.size = size;
        this.originalName = originalName;
        this.contentType = contentType;
        this.hashAlgorithm = hashAlgorithm;
        this.hashValue = hashValue;
        this.storageKey = storageKey;

        this.id = UUID.randomUUID();
        this.markDeleted = false;
        this.createdAt = Instant.now();
    }
}
