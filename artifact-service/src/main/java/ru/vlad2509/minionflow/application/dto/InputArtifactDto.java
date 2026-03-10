package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.InputType;

public record InputArtifactDto(
        ArtifactDto artifact,
        String alias,
        InputType inputType
) {

    public static InputArtifactDto fromDto(ArtifactDto artifact, String alias, InputType type) {
        return new InputArtifactDto(artifact, alias, type);
    }

}
