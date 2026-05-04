package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.InputArtifact;
import ru.vlad2509.minionflow.domain.model.enums.InputType;

public record InputArtifactDto(
        ArtifactDto artifact,
        String alias,
        InputType inputType
) {

    public static InputArtifactDto fromDomain(InputArtifact inputArtifact) {
        return new InputArtifactDto(ArtifactDto.fromDomain(inputArtifact), inputArtifact.getAlias(), inputArtifact.getInputType());
    }

}
