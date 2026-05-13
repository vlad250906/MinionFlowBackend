package ru.vlad2509.minionflow.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.MyApplication;
import ru.vlad2509.minionflow.api.dto.websocket.WebSocketServerMessage;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.WebSocketChannelInfo;
import ru.vlad2509.minionflow.application.dto.WebSocketConnectionState;
import ru.vlad2509.minionflow.application.dto.engine.BaseTaskState;
import ru.vlad2509.minionflow.application.dto.messaging.WebSocketEvent;
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

    private final Map<String, Set<WebSocketConnectionState>> byChannel = new ConcurrentHashMap<>();
    private final Map<WebSocketConnection, Set<WebSocketConnectionState>> byConnection = new ConcurrentHashMap<>();

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
        WebSocketConnectionState state = new WebSocketConnectionState(connection, channel);
        WebSocketChannelInfo info = null;
        if (!byChannel.containsKey(channel))
            tryBind = true;
        byChannel.computeIfAbsent(channel, ign -> ConcurrentHashMap.newKeySet()).add(state);
        byConnection.computeIfAbsent(connection, ign -> ConcurrentHashMap.newKeySet()).add(state);
        if (tryBind && (info = channelFactory.recognise(channel)) != null)
            listener.routingBind(info);
    }

    public void unsubscribe(WebSocketConnection connection, String channel) {
        Set<WebSocketConnectionState> connections = byChannel.get(channel);
        if (connections != null) {
            connections.remove(new WebSocketConnectionState(connection, channel));
            if (connections.isEmpty()) {
                byChannel.remove(channel);
                listener.routingUnbind(channelFactory.recognise(channel));
            }
        }

        Set<WebSocketConnectionState> channels = byConnection.get(connection);
        if (channels != null) {
            channels.remove(new WebSocketConnectionState(connection, channel));
            if (channels.isEmpty()) {
                byConnection.remove(connection);
            }
        }
    }

    public void remove(WebSocketConnection connection) {
        Set<WebSocketConnectionState> channels = byConnection.remove(connection);
        if (channels == null)
            return;

        for (WebSocketConnectionState state : channels) {
            Set<WebSocketConnectionState> connections = byChannel.get(state.getChannel());
            if (connections != null) {
                connections.remove(state);
                if (connections.isEmpty()) {
                    byChannel.remove(state.getChannel());
                }
            }
        }
    }

    public void publish(String channel, long seq, JsonNode message) {
        Set<WebSocketConnectionState> connections = byChannel.getOrDefault(channel, Set.of());
        for (WebSocketConnectionState state : connections) {
            state.publishIfNewer(seq, WebSocketServerMessage.event(channel, message));
        }
    }

    public ApiException authorize(UserContext userContext, String channel) {
        UUID microtaskId = null;
        if ((microtaskId = channelFactory.isMicrotaskLogs(channel)) != null) {
            // FIXME: у swarm тасков невозможно получить microtasks.
            return null;
//            UUID taskId = taskEngine.getTaskByMicrotaskId(microtaskId).orElse(null);
//            if (taskId == null)
//                return new ApiException(ApiError.MICROTASK_NOT_FOUND);
//            return authorizeTransactional(userContext, taskId);
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
            return new ApiException(ApiError.TASK_NOT_FOUND);
        UUID projectId = taskRun.getProjectId();
        return tokenService.authorizeNoThrow(userContext, projectId, ProjectPermission.TASK_READ);
    }

    public void onStartup(@Observes StartupEvent ev) {
        listener.setEventHandler(event -> this.publish(event.channel(), event.seq(), event.content()));
        listener.startListening();
    }

}
