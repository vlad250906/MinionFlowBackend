package ru.vlad2509.minionflow.domain.model;

import ru.vlad2509.minionflow.domain.model.enums.ArtifactType;

import java.time.Instant;
import java.util.UUID;

public class Artifact {

    private final UUID id;
    private final UUID projectId;
    private final UUID userId;
    private long size;
    private String originalName;
    private String contentType;
    private final Instant createdAt;
    private final ArtifactType type;
    private StorageIdentifier storageIdentifier;

    public Artifact(UUID projectId, UUID userId, long size, String originalName, String contentType, ArtifactType type, StorageIdentifier storageIdentifier) {
        this.projectId = projectId;
        this.userId = userId;
        this.size = size;
        this.originalName = originalName;
        this.contentType = contentType;
        this.type = type;
        this.storageIdentifier = storageIdentifier;

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public Artifact(UUID id, UUID projectId, UUID userId, long size, String originalName, String contentType, Instant createdAt, ArtifactType type, StorageIdentifier storageIdentifier) {
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

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getUserId() {
        return userId;
    }

    public long getSize() {
        return size;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ArtifactType getType() {
        return type;
    }

    public StorageIdentifier getStorageIdentifier() {
        return storageIdentifier;
    }

    public void update(String contentType, String originalName, long size, StorageIdentifier identifier){
        this.contentType = contentType;
        this.originalName = originalName;
        this.size = size;
        this.storageIdentifier = identifier;
    }
}
