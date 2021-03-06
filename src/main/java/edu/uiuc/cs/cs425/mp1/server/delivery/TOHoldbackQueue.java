package edu.uiuc.cs.cs425.mp1.server.delivery;

import edu.uiuc.cs.cs425.mp1.data.Message;

import java.util.*;

/**
 * wrapper for things needed for totally ordered algo
 */

public class TOHoldbackQueue {

    //map id to message
    private HashMap<Integer, Message> storedMessages;
    private PriorityQueue<Message> sequencerOrderings;
    //store messages in proper order to be sent out

    public TOHoldbackQueue() {
        storedMessages = new HashMap<>();
        sequencerOrderings = new PriorityQueue<>(new SequenceMessageComparator());
    }

    public void pushOrdering(Message sequencerMessage) {
        sequencerOrderings.add(sequencerMessage);
    }

    public void addMessage(Message message) {
        storedMessages.put(message.getMessageId(), message);
    }

    public Message getMessage(int messageId) {
        return storedMessages.get(messageId);
    }

    public Message popOrdering() {
        return sequencerOrderings.remove();
    }

    public Message peekOrdering() {
        return sequencerOrderings.peek();
    }

    public void removeMessage(int messageId) {
        storedMessages.remove(messageId);
    }

    public boolean noOrderings() {
        return sequencerOrderings.isEmpty();
    }

    public boolean containsMessage(int messageId) {
        return storedMessages.containsKey(messageId);
    }

    //makes sure that messages are in proper order to be sent out
    class SequenceMessageComparator implements Comparator<Message> {

        @Override
        public int compare(Message m1, Message m2) {
            if (m1.getSequencerOrder() < m2.getSequencerOrder()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
