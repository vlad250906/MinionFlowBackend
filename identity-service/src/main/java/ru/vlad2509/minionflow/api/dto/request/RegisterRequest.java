package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.vlad2509.minionflow.domain.EmailVo;
import ru.vlad2509.minionflow.domain.UsernameVo;

public record RegisterRequest(
        @Valid @NotNull
        EmailVo email,
        @Valid @NotNull
        UsernameVo username,
        @NotEmpty
        String password
) {
}
