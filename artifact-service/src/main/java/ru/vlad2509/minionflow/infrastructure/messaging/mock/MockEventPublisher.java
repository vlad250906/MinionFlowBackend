package ru.vlad2509.minionflow.infrastructure.messaging.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.infrastructure.messaging.EventPublisher;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.ConnectionManager;

import java.io.IOException;
import java.util.Random;

@ApplicationScoped
public class MockEventPublisher extends EventPublisher<MockMessage> {

    @Inject
    ConnectionManager connectionManager;

    private static final String QUEUE = "test-queue";

    private static final Logger LOG = LoggerFactory.getLogger(MockEventPublisher.class.getName());

    public MockEventPublisher() {
        super(QUEUE);
    }

    void startup(@Observes StartupEvent event) {}

    @Scheduled(every = "1s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void mockSend(){
        Random random = new Random();
        MockMessage message = new MockMessage("This is a mock message!", random.nextInt(0, 10000));
        LOG.info("mockSend message: {}", message);
        super.publish(message);
    }

    @Override
    protected Channel setupChannel() {
        Channel channel = connectionManager.requestChannel();

        try{
            channel.exchangeDeclare("global", BuiltinExchangeType.TOPIC, true);
            channel.queueDeclare(QUEUE, true, false, false, null);
            channel.queueBind(QUEUE, "global", QUEUE);
        }catch (IOException e){
           throw new RuntimeException(e);
        }

        super.rabbitService.enableConfirmListener(channel, super.outboxService::ack, super.outboxService::nack);

        return channel;
    }

    @Override
    protected String serializeMessage(MockMessage message) {
        try{
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
