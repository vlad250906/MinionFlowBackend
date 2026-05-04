package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ArtifactEntity;

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

    public static ArtifactDto fromDomain(Artifact artifact) {
        return new ArtifactDto(artifact.getId(), artifact.getSize(), artifact.getOriginalName(), artifact.getContentType(),
                artifact.getCreatedAt(), artifact.getUserId());
    }

}
