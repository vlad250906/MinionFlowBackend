package ru.vlad2509.minionflow.infrastructure.messaging.rabbit;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConfirmTracker {

    private RabbitPool rabbitPool;

    private final ConcurrentNavigableMap<Long, String> sentMessages = new ConcurrentSkipListMap<>();
    private final Consumer<String> onAck;
    private final BiConsumer<String, String> onNack;
    private boolean shouldProcess = true;

    public ConfirmTracker(RabbitPool pool, Consumer<String> onAck, BiConsumer<String, String> onNack) {
        this.rabbitPool = pool;
        this.onAck = onAck;
        this.onNack = onNack;
    }

    public void addMessage(long seqNo, String messageId){
        sentMessages.put(seqNo, messageId);
    }

    public void processSeq(long seq, boolean multiple, String error) {
        //System.out.println("PROCESS SEQ: " + seq);
        if (!shouldProcess)
            return;
        List<String> toConfirm = new ArrayList<String>();

        if (multiple) {
            NavigableMap<Long, String> toConfirmMap = sentMessages.headMap(seq, true);
            toConfirm.addAll(toConfirmMap.values());
            toConfirmMap.clear();
        } else {
            toConfirm.add(sentMessages.remove(seq));
        }

        for (String messageId : toConfirm) {
            if (error == null) {
                confirm(messageId);
            } else {
                fail(messageId, error);
            }
        }
    }

    public void failAll(String error) {
        if (!shouldProcess)
            return;

        List<String> toConfirm = new ArrayList<String>(sentMessages.values());
        sentMessages.clear();
        for (String messageId : toConfirm) {
            fail(messageId, error);
        }
        shouldProcess = false;
    }

    private void confirm(String messageId) {
        rabbitPool.execute(() -> onAck.accept(messageId));
    }

    private void fail(String messageId, String error) {
        rabbitPool.execute(() -> onNack.accept(messageId, error));
    }
}
