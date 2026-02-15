package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestQuery;

public record PasswordChangeRequest(
        @NotEmpty
        String oldPassword,
        @NotEmpty
        String newPassword
) {
}
