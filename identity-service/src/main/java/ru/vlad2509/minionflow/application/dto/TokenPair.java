package ru.vlad2509.minionflow.application.dto;

public record TokenPair(
        String accessJWT,
        String refreshJWT
) {
}
