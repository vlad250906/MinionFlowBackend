package ru.vlad2509.minionflow.infrastructure.messaging.events.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.Channel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.application.dto.WebSocketChannelInfo;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLogsBatch;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.dto.messaging.WebSocketEvent;
import ru.vlad2509.minionflow.application.ports.out.TaskPatchNotifier;
import ru.vlad2509.minionflow.application.util.WebSocketChannelFactory;
import ru.vlad2509.minionflow.infrastructure.messaging.EventPublisher;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.ConnectionManager;

import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class WebSocketEventPublisher extends EventPublisher<WebSocketEvent> implements TaskPatchNotifier {

    @Inject
    ConnectionManager connectionManager;

    @Inject
    WebSocketChannelFactory websocketChannelFactory;

    @Inject
    ObjectMapper objectMapper;

    public WebSocketEventPublisher() {
        super("websocket");
    }

    @Override
    public void sendStatelessStatePatch(StatelessTaskState state) {
        WebSocketChannelInfo info = websocketChannelFactory.taskStatePatches(state.taskId());
        super.publishDirect(info.bindingName(), new WebSocketEvent("taskPatch",
                info.webSocketName(), state.seq(), encode(state)));
    }

    @Override
    public void sendSwarmStatePatch(SwarmTaskState state) {
        WebSocketChannelInfo info = websocketChannelFactory.taskStatePatches(state.taskId());
        super.publishDirect(info.bindingName(), new WebSocketEvent("taskPatch",
                info.webSocketName(), state.seq(), encode(state)));
    }

    @Override
    public void sendLogBatch(MicrotaskLogsBatch batch) {
        WebSocketChannelInfo info = websocketChannelFactory.microtaskLogs(batch.microtaskId());
        super.publishDirect(info.bindingName(), new WebSocketEvent("microtaskLogs",
                info.webSocketName(), -1, encode(batch)));
    }

    @Override
    protected Channel setupChannel() {
        Channel channel = connectionManager.requestChannel();
        //rabbitService.enableConfirmListener(channel, outboxService::ack, outboxService::nack);
        return channel;
    }

    @Override
    protected String serializeMessage(WebSocketEvent message) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode encode(Object obj) {
        return objectMapper.valueToTree(obj);
    }
}
