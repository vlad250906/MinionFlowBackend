package ru.vlad2509.minionflow.api.auth.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.vlad2509.minionflow.domain.vo.EmailVo;

public record RecoveryBeginRequest(
        @Valid @NotNull
        EmailVo email
) {
}
