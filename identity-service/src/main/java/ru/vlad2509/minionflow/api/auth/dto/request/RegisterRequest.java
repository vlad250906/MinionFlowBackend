package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Size(max = 100)
        @NotEmpty
        @Email(message = "Некорректный email")
        String email,

        @Size(min = 3, max = 52)
        @NotEmpty
        String username,

        @Size(min = 8, max = 52)
        @NotEmpty
        String password
) {
}
