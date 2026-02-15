package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.domain.vo.UsernameVo;

public record RegisterRequest(
        @Valid @NotNull
        EmailVo email,
        @Valid @NotNull
        UsernameVo username,
        @NotEmpty
        String password
) {
}
