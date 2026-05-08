package ru.vlad2509.minionflow.api.dto.websocket;

import io.vertx.core.json.JsonObject;
import ru.vlad2509.minionflow.application.exception.ApiException;

public record WebSocketServerMessage (
        String type,
        String channel,
        JsonObject payload,
        String code,
        String message
){

    public static WebSocketServerMessage subscribed(String channel) {
        return new WebSocketServerMessage("subscribed", channel, null, null, null);
    }

    public static WebSocketServerMessage unsubscribed(String channel) {
        return new WebSocketServerMessage("unsubscribed", channel, null, null, null);
    }

    public static WebSocketServerMessage ok(String channel) {
        return new WebSocketServerMessage("ok", channel, null, null, null);
    }

    public static WebSocketServerMessage event(String channel, JsonObject payload) {
        return new WebSocketServerMessage("event", channel, payload, null, null);
    }

    public static WebSocketServerMessage error(String channel, ApiException apiException) {
        return new WebSocketServerMessage("error", channel, null, String.valueOf(apiException.getHttpStatusCode()), apiException.getMessage());
    }

}
