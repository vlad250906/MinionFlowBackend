package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.InputType;

public record JarArtifactDto(
        ArtifactDto artifact,
        String alias
) {

    public static JarArtifactDto fromDto(ArtifactDto artifact, String alias) {
        return new JarArtifactDto(artifact, alias);
    }

}
