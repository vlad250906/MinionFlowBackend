package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtifactMetaUpdateRequest(

        @NotBlank
        @Size(min = 1, max = 100)
        String alias

) {
}
