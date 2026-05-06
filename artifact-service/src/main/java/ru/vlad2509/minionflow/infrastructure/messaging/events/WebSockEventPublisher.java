package ru.vlad2509.minionflow.infrastructure.messaging.events;

import com.rabbitmq.client.Channel;
import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLogsBatch;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.dto.messaging.WebSocketEvent;
import ru.vlad2509.minionflow.application.ports.out.TaskPatchNotifier;
import ru.vlad2509.minionflow.infrastructure.messaging.EventPublisher;

@ApplicationScoped
public class WebSockEventPublisher extends EventPublisher<WebSocketEvent> implements TaskPatchNotifier {

    // TODO: умный рутинг по websocket очередям
    // у каждого инстанса очередь, адресация по раутинг ключам
    // плюс модифицировать сигнатуру метода, добавить номер отправляемого сообщения (типа seq)

    public WebSockEventPublisher() {
        super("aaaa");
    }

    @Override
    public void sendStatelessStatePatch(StatelessTaskState state) {
        // TODO
    }

    @Override
    public void sendSwarmStatePatch(SwarmTaskState state) {
        // TODO
    }

    @Override
    public void sendLogBatch(MicrotaskLogsBatch batch) {
        // TODO
    }

    @Override
    protected Channel setupChannel() {
        // TODO
        return null;
    }

    @Override
    protected String serializeMessage(WebSocketEvent message) {
        // TODO
        return "";
    }
}
