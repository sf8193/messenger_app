package edu.uiuc.cs.cs425.mp1.data;

import edu.uiuc.cs.cs425.mp1.server.OperationalStore;

import java.io.Serializable;

public class Message implements Serializable {

    private int sourceId;
    private int destId;
    private long timestamp;
    private long networkDelay;
    private String message;
    private int processMessageNumber;
    private boolean directMessage;

    public Message(String message, int sourceId, int destId,
                   long timestamp, long networkDelay, int processMessageNumber, boolean directMessage) {
        this.sourceId = sourceId;
        this.destId = destId;
        this.timestamp = timestamp;
        this.networkDelay = networkDelay;
        this.message = message;
        this.processMessageNumber = processMessageNumber;
        this.directMessage = directMessage;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getDestId() {
        return destId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getProcessMessageNumber() {
        return processMessageNumber;
    }

    public long getNetworkDelay() {
        return networkDelay;
    }

    public boolean isDirectMessage() {
        return directMessage;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\tsourceId: " + sourceId + "\n" +
                "\tdestId: " + destId + "\n" +
                "\ttimestamp: " + timestamp + "\n" +
                "\tnetworkDelay: " + networkDelay + "\n" +
                "\tprocessMessageNumber: " + processMessageNumber + "\n" +
                "\tmessage: " + message + "\n" +
                "}";

    }

    public static Message getMessage(String message, int sourceId, int destId, long networkDelay) {
        return getMessage(message, sourceId, destId, networkDelay, false);
    }

    public static Message getMessage(String message, int sourceId, int destId, long networkDelay, boolean directMessage) {
        long currentTime = System.currentTimeMillis();
        return new Message(message, sourceId, destId, currentTime, networkDelay, OperationalStore.INSTANCE.fifoClock
                .get(sourceId), directMessage);
    }

    public static Message getIdentifierMessage(int sourceId) {
        return new Message("", sourceId, -1, -1, -1, -1,
                true);
    }
}
