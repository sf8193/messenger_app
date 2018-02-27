package edu.uiuc.cs.cs425.mp1.server.delivery;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.data.Message;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FIFODeliverer extends Deliverer {

    private static final Logger logger = LogManager.getLogger(FIFODeliverer.class.getName());

    public final HashMap<Integer, PriorityQueue<Message>> holdbackQueue;

    public FIFODeliverer() {
        holdbackQueue = new HashMap<>();
        for(Integer id : Configuration.INSTANCE.getSortedIds()) {
            holdbackQueue.put(id, new PriorityQueue<>(10, new MessageNumberComparator()));
        }
    }

    @Override
    protected void handleMessage(Message m) {
        if (m.isDirectMessage()) {
            deliverMessage(m);
        }
        int localProcessMessageNumber = OperationalStore.INSTANCE.getProcessMessageNumber(m.getSourceId());
        if (m.getProcessMessageNumber() == localProcessMessageNumber + 1) {
            // Message is received in correct order. Deliver.
            deliverMessage(m);
            // Update local registry.
            OperationalStore.INSTANCE.incrementFIFOClock(m.getSourceId());
            // Check for local messages available to deliver.
            deliverPendingLocalMessages(m.getSourceId(), m.getProcessMessageNumber());
        } else if (m.getProcessMessageNumber() > localProcessMessageNumber + 1) {
            pushToHoldbackQueue(m);
        } else {
            // Discard message
            logger.debug("Process message number is less than current. Discarding message: " + m);
        }
    }

    private void deliverPendingLocalMessages(int sourceId, int messageNumber) {
        int currentMessageNumber = messageNumber;
        PriorityQueue<Message> pq = holdbackQueue.get(sourceId);
        while (!pq.isEmpty()) {
            Message m = pq.remove();
            if (m.getProcessMessageNumber() == currentMessageNumber + 1) {
                deliverMessage(m);
                currentMessageNumber++;
            } else {
                pq.offer(m);
                return;
            }
        }
    }

    private void pushToHoldbackQueue(Message m) {
        holdbackQueue.get(m.getSourceId()).add(m);
    }

    private static class MessageNumberComparator implements Comparator<Message> {

        @Override
        public int compare(Message m1, Message m2) {
            if (m1.getProcessMessageNumber() < m2.getProcessMessageNumber()) {
                return -1;
            } else if (m1.getProcessMessageNumber() == m2.getProcessMessageNumber()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

}
