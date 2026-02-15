package ru.vlad2509.minionflow.api.dto.response;

import java.util.UUID;

public record RegisterResponse(
        UUID accountId,
        boolean verificationRequired
) {
}
