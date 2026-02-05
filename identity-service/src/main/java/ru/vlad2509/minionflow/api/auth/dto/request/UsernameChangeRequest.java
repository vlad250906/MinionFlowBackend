package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.constraints.Size;

public record UsernameChangeRequest(
        @Size(min = 3, max = 52)
        String newUsername
) {
}
