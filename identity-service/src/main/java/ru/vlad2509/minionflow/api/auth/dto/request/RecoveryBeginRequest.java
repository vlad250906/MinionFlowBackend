package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record RecoveryBeginRequest(
        @Email
        @Size(max = 100)
        String email
) {
}
