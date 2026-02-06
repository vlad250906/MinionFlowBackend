package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RecoveryEndRequest(
        UUID userId,

        UUID verificationToken,

        @Size(min = 8, max = 52)
        @NotEmpty
        String password
) {
}
