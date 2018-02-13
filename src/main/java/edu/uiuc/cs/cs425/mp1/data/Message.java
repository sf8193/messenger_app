package edu.uiuc.cs.cs425.mp1.data;

import java.io.Serializable;

public class Message implements Serializable {

    private int sourceId;
    private long timestamp;
    private long networkDelay;
    private String message;

    public Message(int sourceId, long timestamp, long networkDelay, String message) {
        this.sourceId = sourceId;
        this.timestamp = timestamp;
        this.networkDelay = networkDelay;
        this.message = message;
    }

    public int getSourceId() {
        return sourceId;
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
}
