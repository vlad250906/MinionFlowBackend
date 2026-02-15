package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.constraints.Size;

public record UserInfoChangeRequest(
        @Size(min = 3, max = 52)
        String newUsername
) {
}
