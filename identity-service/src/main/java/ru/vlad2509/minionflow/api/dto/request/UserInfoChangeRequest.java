package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.vlad2509.minionflow.domain.UsernameVo;

public record UserInfoChangeRequest(
        @Valid @NotNull
        UsernameVo newUsername
) {
}
