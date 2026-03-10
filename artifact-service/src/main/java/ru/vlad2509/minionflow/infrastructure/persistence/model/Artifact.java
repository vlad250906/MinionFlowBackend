package ru.vlad2509.minionflow.infrastructure.persistence.model;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.model.ArtifactType;

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
    public long size;

    @Column(nullable = false)
    public String originalName;

    @Column(nullable = false)
    public String contentType;

    @Column(nullable = false)
    public Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public ArtifactType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_identifier_id", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT) // storageidentifier может быть удален только если IsUse == 0, противоречие
    public StorageIdentifier storageIdentifier;

    public Artifact() {
    }

    public Artifact(UUID projectId, UUID userId, ArtifactType type, long size, String originalName,
                    String contentType, StorageIdentifier storageIdentifier) {
        this.projectId = projectId;
        this.userId = userId;
        this.type = type;
        this.size = size;
        this.originalName = originalName;
        this.contentType = contentType;
        this.storageIdentifier = storageIdentifier;

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public String getStorageKey(){
        return storageIdentifier.storageKey;
    }
}
