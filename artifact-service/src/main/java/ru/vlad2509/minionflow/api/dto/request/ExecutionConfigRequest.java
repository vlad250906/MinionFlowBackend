package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfig;

public record ExecutionConfigRequest (
        @NotBlank
        @Size(min = 1, max = 100)
        String alias,

        @Valid
        ExecutionConfig config
) {
}
