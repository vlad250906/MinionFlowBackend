package ru.vlad2509.minionflow.api.auth.dto;

public record JwtPairResponse(

        String accessJWT,
        String refreshJWT

) {
}
