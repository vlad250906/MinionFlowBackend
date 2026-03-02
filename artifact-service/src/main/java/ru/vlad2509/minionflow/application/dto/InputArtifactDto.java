package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.InputType;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Artifact;

import java.time.Instant;
import java.util.UUID;

public record InputArtifactDto(
        ArtifactDto artifact,
        InputType inputType
) {

    public static InputArtifactDto fromDto(ArtifactDto artifact, InputType type) {
        return new InputArtifactDto(artifact, type);
    }

}
