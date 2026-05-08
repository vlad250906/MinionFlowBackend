package ru.vlad2509.minionflow.infrastructure.messaging.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.rabbitmq.client.Channel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLogsBatch;
import ru.vlad2509.minionflow.infrastructure.messaging.EventListener;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.ConnectionManager;

@ApplicationScoped
public class LogBatchEventListener extends EventListener<MicrotaskLogsBatch> {

    @Inject
    ConnectionManager connectionManager;

    private static final String QUEUE = "MICROTASK_LOGS";

    protected LogBatchEventListener() {
        super(QUEUE, true);
    }

    @Override
    protected Channel setupChannel() {
        Channel channel = connectionManager.requestChannel();
        rabbitService.initQueue(channel, QUEUE, true);
        rabbitService.enableConfirmSending(channel);
        rabbitService.setQos(channel, 1); // т.к. в лог батче нету seq, я не смогу гарантировать порядок доставки логов, поэтому такой батлнек((
        return channel;
    }

    @Override
    protected MicrotaskLogsBatch parse(String payload) {
        try {
            ObjectReader objectReader = new ObjectMapper().readerFor(MicrotaskLogsBatch.class);
            return objectReader.readValue(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
