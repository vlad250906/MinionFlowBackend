package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.domain.model.JarArtifact;

public record JarArtifactDto(
        ArtifactDto artifact,
        String alias
) {

    public static JarArtifactDto fromDomain(JarArtifact jarArtifact) {
        return new JarArtifactDto(ArtifactDto.fromDomain(jarArtifact), jarArtifact.getAlias());
    }

}
