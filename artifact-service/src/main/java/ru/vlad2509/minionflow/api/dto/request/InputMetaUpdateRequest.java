package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.vlad2509.minionflow.domain.model.enums.InputType;

public record InputMetaUpdateRequest(

        @NotBlank
        @Size(min = 1, max = 100)
        String alias,

        @NotNull
        InputType inputType
) {
}
