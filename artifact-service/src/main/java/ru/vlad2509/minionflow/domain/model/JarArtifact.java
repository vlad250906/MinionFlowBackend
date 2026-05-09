package ru.vlad2509.minionflow.domain.model;

import ru.vlad2509.minionflow.domain.model.enums.ArtifactType;

import java.time.Instant;
import java.util.UUID;

public class JarArtifact extends Artifact {

    private final Long internalId;
    private String alias;

    public JarArtifact(UUID id, UUID projectId, UUID userId, long size, String originalName, String contentType,
                       Instant createdAt, ArtifactType type, StorageIdentifier storageIdentifier, Long internalId,
                       String alias) {
        super(id, projectId, userId, size, originalName, contentType, createdAt, type, storageIdentifier);
        this.internalId = internalId;
        this.alias = alias;
    }

    public JarArtifact(UUID projectId, UUID userId, long size, String originalName, String contentType,
                       ArtifactType type, StorageIdentifier storageIdentifier, String alias) {
        super(projectId, userId, size, originalName, contentType, type, storageIdentifier);
        this.alias = alias;
        this.internalId = null;
    }

    public JarArtifact(Artifact artifact, String alias) {
        super(artifact.getId(), artifact.getProjectId(), artifact.getUserId(), artifact.getSize(), artifact.getOriginalName(),
                artifact.getContentType(), artifact.getCreatedAt(), artifact.getType(), artifact.getStorageIdentifier());
        this.alias = alias;
        this.internalId = null;
    }

    public Long getInternalId() {
        return internalId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
