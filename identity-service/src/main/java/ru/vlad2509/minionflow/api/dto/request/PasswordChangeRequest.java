package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record PasswordChangeRequest(
        @NotEmpty
        String oldPassword,
        @NotEmpty
        String newPassword
) {
}
