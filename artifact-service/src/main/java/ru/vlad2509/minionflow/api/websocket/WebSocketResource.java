package ru.vlad2509.minionflow.api.websocket;

import io.quarkus.security.Authenticated;
import io.quarkus.websockets.next.*;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.api.dto.websocket.WebSocketClientMessage;
import ru.vlad2509.minionflow.api.dto.websocket.WebSocketServerMessage;
import ru.vlad2509.minionflow.application.WebSocketService;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.TokenService;

@Authenticated
@WebSocket(path = "/ws/v1")
public class WebSocketResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    WebSocketService service;

    @Inject
    TokenService tokenService;

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketResource.class);

    @OnTextMessage
    public WebSocketServerMessage onMessage(WebSocketClientMessage msg, WebSocketConnection connection) {
        return switch (msg.op()) {
            case "subscribe" -> {
                ApiException apiException = service.authorize(tokenService.parseJwt(jwt),msg.channel());
                if (apiException != null)
                    yield WebSocketServerMessage.error(msg.channel(), apiException);

                service.subscribe(connection, msg.channel());
                yield WebSocketServerMessage.subscribed(msg.channel());
            }

            case "unsubscribe" -> {
                ApiException apiException = service.authorize(tokenService.parseJwt(jwt), msg.channel());

                if (apiException != null)
                    yield WebSocketServerMessage.error(msg.channel(), apiException);

                service.unsubscribe(connection, msg.channel());
                yield WebSocketServerMessage.unsubscribed(msg.channel());
            }

            default -> WebSocketServerMessage.error(msg.channel(), new ApiException(ApiError.BAD_OP));
        };
    }

    @OnClose
    public void onClose(WebSocketConnection connection) {
        service.remove(connection);
    }

    @OnError
    public WebSocketServerMessage onError(Throwable t, WebSocketConnection connection) {
        service.remove(connection);
        LOG.error("unknown error: ", t);
        return WebSocketServerMessage.error(null, new ApiException(ApiError.UNEXPECTED_ERROR));
    }
}