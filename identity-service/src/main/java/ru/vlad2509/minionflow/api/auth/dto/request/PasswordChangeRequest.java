package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestQuery;

public record PasswordChangeRequest(
        @Size(min = 8, max = 52)
        @NotEmpty
        String oldPassword,

        @Size(min = 8, max = 52)
        @NotEmpty
        String newPassword
) {
}
