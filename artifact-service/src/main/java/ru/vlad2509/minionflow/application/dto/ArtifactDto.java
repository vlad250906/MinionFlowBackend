package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.infrastructure.persistence.model.Artifact;

import java.time.Instant;
import java.util.UUID;

public record ArtifactDto(

        UUID artifactId,
        long size,
        String originalName,
        String contentType,
        Instant createdAt,
        UUID ownerId

) {

    public static ArtifactDto fromJpa(Artifact artifact) {
        return new ArtifactDto(artifact.id, artifact.size, artifact.originalName, artifact.contentType,
                artifact.createdAt, artifact.userId);
    }

}
