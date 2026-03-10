package ru.vlad2509.minionflow.api.dto.response;

import ru.vlad2509.minionflow.application.dto.ArtifactDto;

import java.util.List;

public record OutputList(
        List<ArtifactDto> outputs
) {
}
