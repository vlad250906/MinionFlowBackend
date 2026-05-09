package ru.vlad2509.minionflow.application.dto.messaging;

import com.fasterxml.jackson.databind.JsonNode;

public record WebSocketEvent (
        String type,
        String channel,
        long seq,
        JsonNode content
){
}
