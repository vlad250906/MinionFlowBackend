package ru.vlad2509.minionflow.application.dto;

import java.util.UUID;

public record DecodedRefreshToken(UUID userId, UUID sessionId, UUID jwtId) {
}
