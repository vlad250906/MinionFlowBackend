package ru.vlad2509.minionflow.application.dto.messaging;

public record WebSocketEvent (
        String type,
        String channel,
        long seq,
        String content
){
}
