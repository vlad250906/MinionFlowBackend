package ru.vlad2509.minionflow.api.dto.response;

import java.time.Instant;

public record JwtPairResponse(

        String accessJWT,
        Instant validUntil

) {
}
