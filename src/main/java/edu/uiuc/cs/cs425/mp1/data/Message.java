package edu.uiuc.cs.cs425.mp1.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Message implements Serializable {

    private int messageId;
    private String message;
    private int sourceId;
    private int destId;
    private long timestamp;
    private long networkDelay;
    private int processMessageNumber;
    private boolean directMessage;
    private int sequencerOrder;
    private boolean sequencerMessage;
    private Map<Integer, Integer> vectorTimestamp;

    public Message(int messageId, String message, int sourceId, int destId, long timestamp, long networkDelay,
                   int processMessageNumber, Map<Integer, Integer> vectorTimestamp,
                   boolean directMessage, int sequencerOrder, boolean sequencerMessage) {
        this.messageId = messageId;
        this.message = message;
        this.sourceId = sourceId;
        this.destId = destId;
        this.timestamp = timestamp;
        this.networkDelay = networkDelay;
        this.processMessageNumber = processMessageNumber;
        this.vectorTimestamp = new HashMap<>(vectorTimestamp);
        this.directMessage = directMessage;
        this.sequencerOrder = sequencerOrder;
        this.sequencerMessage = sequencerMessage;
    }

    public Message(Message m) {
        this.messageId = m.messageId;
        this.message = m.message;
        this.sourceId = m.sourceId;
        this.destId = m.destId;
        this.timestamp = m.timestamp;
        this.networkDelay = m.networkDelay;
        this.processMessageNumber = m.processMessageNumber;
        this.vectorTimestamp = new HashMap<>(m.vectorTimestamp);
        this.directMessage = m.directMessage;
        this.sequencerOrder = m.sequencerOrder;
        this.sequencerMessage = m.sequencerMessage;
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

    public int getMessageId() {
        return messageId;
    }

    public int getSequencerOrder() {
        return sequencerOrder;
    }

    public Map<Integer, Integer> getVectorTimestamp() {
        return vectorTimestamp;
    }

    public boolean isDirectMessage() {
        return directMessage;
    }

    public boolean isSequencerMessage() {
        return sequencerMessage;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\tmessageId: " + messageId + "\n" +
                "\tsourceId: " + sourceId + "\n" +
                "\tdestId: " + destId + "\n" +
                "\ttimestamp: " + timestamp + "\n" +
                "\tnetworkDelay: " + networkDelay + "\n" +
                "\tprocessMessageNumber: " + processMessageNumber + "\n" +
                "\tmessage: " + message + "\n" +
                "\tdirectMessage: " + directMessage + "\n" +
                "\tsequencerOrder: " + sequencerOrder + "\n" +
                "\tsequencerMessage: " + sequencerMessage + "\n" +
                "\tvectorTimestamp: " + vectorTimestamp.entrySet().stream()
                    .map(a ->
                            String.format("(%d,%d)",
                                    a.getKey(), a.getValue())).collect(Collectors.joining(",")) + "\n" +
                "}";

    }

    public static Message timestampMessage(Message orig) {
        Message message = new Message(orig);
        message.timestamp = System.currentTimeMillis();
        return message;
    }

    public static Message newMessageDelay(Message orig, long networkDelay) {
        Message message = new Message(orig);
        message.networkDelay = networkDelay;
        return message;
    }

}
