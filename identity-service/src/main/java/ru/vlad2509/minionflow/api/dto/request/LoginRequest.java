package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.domain.vo.UsernameVo;

public record LoginRequest(

        @Valid
        EmailVo email,
        @Valid
        UsernameVo username,
        @NotEmpty
        String password
) {

    public LoginRequest {
        if ((email != null && username != null) || (email == null && username == null))
            throw new IllegalArgumentException("Provide exactly one of: email, username");
    }
}
