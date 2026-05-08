package ru.vlad2509.minionflow.application;

import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.MyApplication;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.WebSocketChannelInfo;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.ports.out.TaskEngine;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.application.util.WebSocketChannelFactory;
import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.domain.model.enums.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.messaging.events.websocket.WebSocketEventListener;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.TaskRunRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class WebSocketService {

    private final Map<String, Set<WebSocketConnection>> byChannel = new ConcurrentHashMap<>();
    private final Map<WebSocketConnection, Set<String>> byConnection = new ConcurrentHashMap<>();

    @Inject
    WebSocketEventListener listener;

    @Inject
    WebSocketChannelFactory channelFactory;

    @Named(MyApplication.ENGINE_USED)
    @Inject
    TaskEngine taskEngine;

    @Inject
    TaskRunRepository taskRunRepository;

    @Inject
    TokenService tokenService;


    public void subscribe(WebSocketConnection connection, String channel) {
        boolean tryBind = false;
        WebSocketChannelInfo info = null;
        if (!byChannel.containsKey(channel))
            tryBind = true;
        byChannel.computeIfAbsent(channel, ign -> ConcurrentHashMap.newKeySet()).add(connection);
        byConnection.computeIfAbsent(connection, ign -> ConcurrentHashMap.newKeySet()).add(channel);
        if (tryBind && (info = channelFactory.recognise(channel)) != null)
            listener.routingBind(info);
    }

    public void unsubscribe(WebSocketConnection connection, String channel) {
        Set<WebSocketConnection> connections = byChannel.get(channel);
        if (connections != null) {
            connections.remove(connection);
            if (connections.isEmpty()) {
                byChannel.remove(channel);
                listener.routingUnbind(channelFactory.recognise(channel));
            }
        }

        Set<String> channels = byConnection.get(connection);
        if (channels != null) {
            channels.remove(channel);
            if (channels.isEmpty()) {
                byConnection.remove(connection);
            }
        }
    }

    public void remove(WebSocketConnection connection) {
        Set<String> channels = byConnection.remove(connection);
        if (channels == null)
            return;

        for (String channel : channels) {
            Set<WebSocketConnection> connections = byChannel.get(channel);
            if (connections != null) {
                connections.remove(connection);
                if (connections.isEmpty()) {
                    byChannel.remove(channel);
                }
            }
        }
    }

    public Uni<Void> publish(String channel, Object message) {
        Set<WebSocketConnection> connections = byChannel.getOrDefault(channel, Set.of());
        List<Uni<Void>> sends = connections.stream().map(connection -> connection.sendText(message)).toList();

        if (sends.isEmpty())
            return Uni.createFrom().voidItem();

        return Uni.combine().all().unis(sends).discardItems();
    }

    public ApiException authorize(UserContext userContext, String channel) {
        UUID microtaskId = null;
        if ((microtaskId = channelFactory.isMicrotaskLogs(channel)) != null) {
            UUID taskId = taskEngine.getTaskByMicrotaskId(microtaskId).orElse(null);
            if (taskId == null)
                return new ApiException(ApiError.MICROTASK_NOT_FOUND);
            return authorizeTransactional(userContext, taskId);
        }

        UUID taskId = null;
        if ((taskId = channelFactory.isTaskStatePatch(channel)) != null) {
            return authorizeTransactional(userContext, taskId);
        }

        return new ApiException(ApiError.UNDEFINED_CHANNEL);
    }

    @Transactional
    ApiException authorizeTransactional(UserContext userContext, UUID taskId) {
        TaskRun taskRun = taskRunRepository.findById(taskId).orElse(null);
        if (taskRun == null)
            return new ApiException(ApiError.MICROTASK_NOT_FOUND);
        UUID projectId = taskRun.getProjectId();
        return tokenService.authorizeNoThrow(userContext, projectId, ProjectPermission.TASK_READ);
    }

}
