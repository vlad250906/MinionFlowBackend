package ru.vlad2509.minionflow.api.websocket;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import ru.vlad2509.minionflow.api.dto.websocket.WebSocketClientMessage;
import ru.vlad2509.minionflow.api.dto.websocket.WebSocketServerMessage;
import ru.vlad2509.minionflow.application.WebSocketService;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.TokenService;

@WebSocket(path = "/ws/v1")
public class ServiceWebSocket {

    @Inject
    JsonWebToken jwt;

    @Inject
    WebSocketService service;

    @Inject
    TokenService tokenService;

    @OnTextMessage
    public Uni<WebSocketServerMessage> onMessage(WebSocketClientMessage msg, WebSocketConnection connection) {
        return switch (msg.op()) {
            case "subscribe" -> {
                ApiException apiException = service.authorize(tokenService.parseJwt(jwt), msg.channel());
                if(apiException != null)
                    yield Uni.createFrom().item(WebSocketServerMessage.error(msg.channel(), apiException));
                service.subscribe(connection, msg.channel());
                yield Uni.createFrom().item(WebSocketServerMessage.subscribed(msg.channel()));
            }
            case "unsubscribe" -> {
                ApiException apiException = service.authorize(tokenService.parseJwt(jwt), msg.channel());
                if(apiException != null)
                    yield Uni.createFrom().item(WebSocketServerMessage.error(msg.channel(), apiException));
                service.unsubscribe(connection, msg.channel());
                yield Uni.createFrom().item(WebSocketServerMessage.unsubscribed(msg.channel()));
            }
            default -> Uni.createFrom().item(
                    WebSocketServerMessage.error(msg.channel(), new ApiException(ApiError.BAD_OP))
            );
        };
    }

    @OnClose
    public void onClose(WebSocketConnection connection) {
        service.remove(connection);
    }

}
