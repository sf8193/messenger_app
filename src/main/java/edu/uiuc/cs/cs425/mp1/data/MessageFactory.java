package edu.uiuc.cs.cs425.mp1.data;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageFactory {

    private static volatile AtomicInteger messageCounter = new AtomicInteger(0);

    private static volatile AtomicInteger numberOfProcesses = null;

    private static int getNumberOfProcesses() {
        if (numberOfProcesses == null) {
            numberOfProcesses = new AtomicInteger(Configuration.INSTANCE.getSortedIds().size());
        }
        return numberOfProcesses.get();
    }

    public static int getMessageId(int id) {
        return getMessageId(id, getNumberOfProcesses());
    }

    private static int getMessageId(int id, int numberOfProcesses) {
        return id + numberOfProcesses * messageCounter.getAndIncrement();
    }

    public static Message createMessage(int messageId, String message, int sourceId, int destId, long networkDelay) {
        return createMessage(messageId, message, sourceId, destId, networkDelay, false);
    }

    public static Message createMessage(int messageId, String message, int sourceId, int destId,
                                        long networkDelay, boolean directMessage) {
        return createMessage(messageId, message, sourceId, destId, networkDelay, directMessage, -1, false);
    }

    public static Message createMessage(int messageId, String message, int sourceId, int destId, long networkDelay,
                                        boolean directMessage, int sequencerOrder, boolean sequencerMessage) {
        long currentTime = System.currentTimeMillis();
        return new Message(messageId, message, sourceId, destId, currentTime,
                networkDelay,
                OperationalStore
                .INSTANCE.fifoClock
                .get(sourceId), OperationalStore.INSTANCE.vectorClock, directMessage, sequencerOrder,
                sequencerMessage);
    }

    public static Message createIdentifierMessage(int sourceId) {
        return new Message(getMessageId(sourceId),"", sourceId, -1, -1, -1, -1,
                Map.of(),true, -1, false);
    }

}
