package ru.vlad2509.minionflow.api.dto.websocket;

import io.vertx.core.json.JsonObject;

public record WebSocketClientMessage (
        String op,
        String channel
){
}
