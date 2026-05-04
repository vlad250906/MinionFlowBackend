package ru.vlad2509.minionflow.infrastructure.persistence.model;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.domain.model.enums.ArtifactType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "artifacts")
public class ArtifactEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "project_id", nullable = false)
    public UUID projectId;

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Column(name = "size", nullable = false)
    public long size;

    @Column(name = "original_name", nullable = false)
    public String originalName;

    @Column(name = "content_type", nullable = false)
    public String contentType;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
    public Instant createdAt;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    public ArtifactType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_identifier_id", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    // storageidentifier может быть удален только если IsUse == 0, противоречие
    public StorageIdentifierEntity storageIdentifier;

    public ArtifactEntity() {
    }

    public ArtifactEntity(UUID id, UUID projectId, UUID userId, long size, String originalName, String contentType, Instant createdAt, ArtifactType type, StorageIdentifierEntity storageIdentifier) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.size = size;
        this.originalName = originalName;
        this.contentType = contentType;
        this.createdAt = createdAt;
        this.type = type;
        this.storageIdentifier = storageIdentifier;
    }

    public String getStorageKey() {
        return storageIdentifier.storageKey;
    }

    public Artifact toDomain() {
        return new Artifact(id, projectId, userId, size, originalName, contentType, createdAt, type, storageIdentifier.toDomain());
    }

    public static ArtifactEntity fromDomain(Artifact artifact, StorageIdentifierEntity storageIdentifier) {
        return new ArtifactEntity(artifact.getId(), artifact.getProjectId(), artifact.getUserId(), artifact.getSize(),
                artifact.getOriginalName(), artifact.getContentType(), artifact.getCreatedAt(), artifact.getType(), storageIdentifier);
    }
}
