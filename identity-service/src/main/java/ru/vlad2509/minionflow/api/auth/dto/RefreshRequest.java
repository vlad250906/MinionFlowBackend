package ru.vlad2509.minionflow.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record RefreshRequest(

        @NotEmpty
        String refreshJWT

) {
}
