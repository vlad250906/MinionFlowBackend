package ru.vlad2509.minionflow.api.auth.dto.response;

public record JwtPairResponse(

        String accessJWT,
        String refreshJWT

) {
}
