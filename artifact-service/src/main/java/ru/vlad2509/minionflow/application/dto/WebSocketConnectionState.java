package ru.vlad2509.minionflow.application.dto;

import io.quarkus.websockets.next.WebSocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class WebSocketConnectionState {
    private final WebSocketConnection connection;
    private final String channel;

    private transient final Object lock = new Object();
    private transient long lastSeq = Long.MIN_VALUE;
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketConnectionState.class);

    public WebSocketConnectionState(WebSocketConnection connection, String channel) {
        this.connection = connection;
        this.channel = channel;
    }

    public WebSocketConnection getConnection() {
        return connection;
    }

    public String getChannel() {
        return channel;
    }

    public void publishIfNewer(long seq, Object content) {
        synchronized (lock) {
            if (seq <= lastSeq)
                return;

            lastSeq = seq;

            if (!connection.isOpen())
                return;

            connection.sendText(content).subscribe().with(ignored -> {},
                    failure -> LOG.error("WebSocket send failed", failure));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketConnectionState state = (WebSocketConnectionState) o;
        return Objects.equals(connection, state.connection) && Objects.equals(channel, state.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connection, channel);
    }
}
