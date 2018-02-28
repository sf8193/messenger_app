package edu.uiuc.cs.cs425.mp1.data;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;
import edu.uiuc.cs.cs425.mp1.util.ServerUtils;

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

    private static Message baseCreateMessage(int messageId, String message, int sourceId, int destId,
                                             int processMessageNumber, Map<Integer, Integer> vectorTimestamp,
                                             boolean directMessage, int sequencerOrder, boolean isSequencerMessage) {
        long currentTime = System.currentTimeMillis();
        int networkDelay = ServerUtils.getRandomNetworkDelay();
        return new Message(messageId, message, sourceId, destId, currentTime, networkDelay,
                processMessageNumber, vectorTimestamp, directMessage, sequencerOrder, isSequencerMessage);

    }

    public static Message createMessage(int messageId, String message, int sourceId, int destId) {
        return createMessage(messageId, message, sourceId, destId, false);
    }

    public static Message createMessage(int messageId, String message, int sourceId, int destId,
                                        boolean directMessage) {
        return createMessage(messageId, message, sourceId, destId, directMessage, -1, false);
    }

    public static Message createMessage(int messageId, String message, int sourceId, int destId,
                                        boolean directMessage, int sequencerOrder, boolean sequencerMessage) {
        return baseCreateMessage(messageId, message, sourceId, destId,
                OperationalStore.INSTANCE.fifoClock.get(sourceId), OperationalStore.INSTANCE.vectorClock,
                directMessage, sequencerOrder, sequencerMessage);
    }

    public static Message createSequencerMessage(int messageId, String message, int sourceId, int destId, int sequencerOrder) {
        return baseCreateMessage(messageId, message, sourceId, destId, -1, Map.of(),
                false, sequencerOrder, true);
    }

    public static Message createIdentifierMessage(int sourceId) {
        return baseCreateMessage(getMessageId(sourceId),"IDENTIFIER_MSG", sourceId, -1,
                -1, Map.of(), true,-1,false);
    }

}
