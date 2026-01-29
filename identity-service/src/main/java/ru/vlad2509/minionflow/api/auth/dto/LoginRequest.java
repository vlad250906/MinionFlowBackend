package ru.vlad2509.minionflow.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email
        @Size(max = 100)
        String email,

        @Size(min = 3, max = 52)
        String username,

        @Size(min = 8, max = 52)
        @NotEmpty
        String password
) {
}
