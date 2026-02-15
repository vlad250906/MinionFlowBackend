package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.domain.vo.UsernameVo;

public record LoginRequest(

        @Valid @NotNull
        EmailVo email,
        @Valid @NotNull
        UsernameVo username,
        @NotEmpty
        String password
) {
}
