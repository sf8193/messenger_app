package edu.uiuc.cs.cs425.mp1.data;

import java.io.Serializable;

public class Message implements Serializable {

    private int sourceId;
    private int destId;
    private long timestamp;
    private long networkDelay;
    private String message;

    public Message(String message, int sourceId, int destId, long timestamp, long networkDelay) {
        this.sourceId = sourceId;
        this.timestamp = timestamp;
        this.networkDelay = networkDelay;
        this.message = message;
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

    public long getNetworkDelay() {
        return networkDelay;
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
                "\tmessage: " + message + "\n" +
                "}";

    }

    public static Message getMessage(String message, int sourceId, int destId, long networkDelay) {
        long currentTime = System.currentTimeMillis();
        return new Message(message, sourceId, destId, currentTime, networkDelay);
    }

    public static Message getIdentifierMessage(int sourceId) {
        return new Message("", sourceId, -1, -1, -1);
    }
}
