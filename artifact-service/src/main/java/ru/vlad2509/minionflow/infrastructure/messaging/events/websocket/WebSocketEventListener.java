package ru.vlad2509.minionflow.infrastructure.messaging.events.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.rabbitmq.client.Channel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.application.dto.WebSocketChannelInfo;
import ru.vlad2509.minionflow.application.dto.messaging.WebSocketEvent;
import ru.vlad2509.minionflow.infrastructure.messaging.EventListener;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.ConnectionManager;

@Singleton
public class WebSocketEventListener extends EventListener<WebSocketEvent> {

    private static final String QUEUE_NAME = "websocket-";
    private final int instanceId;
    private final ConnectionManager connectionManager;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    protected WebSocketEventListener(
            @ConfigProperty(name = "service-common.instance-id", defaultValue = "1") int instanceId,
            ConnectionManager connectionManager
    ) {
        super(QUEUE_NAME + instanceId, true);
        this.instanceId = instanceId;
        this.connectionManager = connectionManager;
    }

    public void routingBind(WebSocketChannelInfo channelInfo) {
        Channel channel = super.getChannel();
        synchronized (channel){
            rabbitService.addBinding(channel, QUEUE_NAME+instanceId, channelInfo.bindingName());
        }
    }

    public void routingUnbind(WebSocketChannelInfo channelInfo) {
        Channel channel = super.getChannel();
        synchronized (channel){
            rabbitService.removeBinding(channel, QUEUE_NAME+instanceId, channelInfo.bindingName());
        }
    }


    @Override
    protected Channel setupChannel() {
        Channel channel = connectionManager.requestChannel();
        rabbitService.deleteQueue(channel, QUEUE_NAME+instanceId); // чистим биндинги
        rabbitService.initQueue(channel, QUEUE_NAME+instanceId, true);
        rabbitService.enableConfirmSending(channel);
        rabbitService.setQos(channel, 1); // нам важна последовательность патчей
        return channel;
    }

    @Override
    protected WebSocketEvent parse(String payload) {
        System.out.println("Websocket got: "+payload);
        try {
            ObjectReader objectReader = objectMapper.readerFor(WebSocketEvent.class);
            return objectReader.readValue(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
