package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.InputType;

public record InputArtifactDto(
        ArtifactDto artifact,
        InputType inputType
) {

    public static InputArtifactDto fromDto(ArtifactDto artifact, InputType type) {
        return new InputArtifactDto(artifact, type);
    }

}
