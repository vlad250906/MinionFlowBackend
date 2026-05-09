package ru.vlad2509.minionflow.domain.model;

import ru.vlad2509.minionflow.domain.model.enums.ArtifactType;
import ru.vlad2509.minionflow.domain.model.enums.InputType;

import java.time.Instant;
import java.util.UUID;

public class InputArtifact extends Artifact{

    private final Long internalId;
    private String alias;
    private InputType inputType;

    public InputArtifact(UUID id, UUID projectId, UUID userId, long size, String originalName, String contentType,
                         Instant createdAt, ArtifactType type, StorageIdentifier storageIdentifier, Long internalId,
                         String alias, InputType inputType) {
        super(id, projectId, userId, size, originalName, contentType, createdAt, type, storageIdentifier);
        this.internalId = internalId;
        this.alias = alias;
        this.inputType = inputType;
    }

    public InputArtifact(UUID projectId, UUID userId, long size, String originalName, String contentType,
                         ArtifactType type, StorageIdentifier storageIdentifier, String alias, InputType inputType) {
        super(projectId, userId, size, originalName, contentType, type, storageIdentifier);
        this.alias = alias;
        this.inputType = inputType;
        this.internalId = null;
    }

    public InputArtifact(Artifact artifact, String alias, InputType inputType) {
        super(artifact.getId(), artifact.getProjectId(), artifact.getUserId(), artifact.getSize(), artifact.getOriginalName(),
                artifact.getContentType(), artifact.getCreatedAt(), artifact.getType(), artifact.getStorageIdentifier());
        this.alias = alias;
        this.inputType = inputType;
        this.internalId = null;
    }

    public Long getInternalId() {
        return internalId;
    }

    public String getAlias() {
        return alias;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }
}
