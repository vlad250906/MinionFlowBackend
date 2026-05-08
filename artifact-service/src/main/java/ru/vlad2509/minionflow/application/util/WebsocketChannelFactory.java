package ru.vlad2509.minionflow.application.util;

import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.application.dto.WebSocketChannelInfo;

import java.util.UUID;

@ApplicationScoped
public class WebsocketChannelFactory {

    public WebSocketChannelInfo microtaskLogs(UUID microtaskId) {
        return new WebSocketChannelInfo("microtasks/" + microtaskId + "/logs", "websocket.microtask-logs."+microtaskId);
    }

    public WebSocketChannelInfo taskStatePatches(UUID taskId) {
        return new WebSocketChannelInfo("tasks/" + taskId + "/state", "websocket.task-state-patches."+taskId);
    }
}
