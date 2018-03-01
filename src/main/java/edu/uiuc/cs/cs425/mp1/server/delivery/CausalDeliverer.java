package edu.uiuc.cs.cs425.mp1.server.delivery;

import edu.uiuc.cs.cs425.mp1.data.Message;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;
import static edu.uiuc.cs.cs425.mp1.util.ServerUtils.incrementMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * class that handles Causally ordered messages
 */
public class CausalDeliverer extends Deliverer {

    private static final Logger logger = LogManager.getLogger(CausalDeliverer.class.getName());

    private final List<Message> holdbackQueue = new LinkedList<>();

    @Override
    protected void handleMessage(Message m) {
        synchronized (OperationalStore.INSTANCE.vectorClock) {
            if (m.isDirectMessage()) {
                deliverMessage(m);
                return;
            }

            //get vector clock from shared memory
            Map<Integer, Integer> currentVectorClock = OperationalStore.INSTANCE.getVectorClock();
            //check if ready for delivery
            if (isReadyForCODelivery(m.getSourceId(), m.getVectorTimestamp(), currentVectorClock)) {
                // Message is received in correct order. Deliver.
                deliverMessage(m);
                // Update local registry.
                incrementMap(m.getSourceId(), currentVectorClock);
                // check for local messages available to deliver.
                deliverPendingLocalMessages(currentVectorClock);
                OperationalStore.INSTANCE.updateVectorClock(currentVectorClock);
            } else {
                pushToHoldbackQueue(m);
            }
        }
    }

    /**
     *
     * @param sourceId
     * @param messageClock
     * @param currentVectorClock
     * @return whether message can be delivered or not
     */
    private boolean isReadyForCODelivery(int sourceId, Map<Integer, Integer> messageClock,
                                         Map<Integer, Integer> currentVectorClock) {

        if (messageClock.get(sourceId) == (currentVectorClock.get(sourceId) + 1)) {
            for (Map.Entry<Integer, Integer> entry : messageClock.entrySet()) {
                int procId = entry.getKey();
                int processMessageNumber = entry.getValue();
                if (procId != sourceId && processMessageNumber > currentVectorClock.get(procId)) {
                    return false;
                }
            }
            return true;
        }
        return false;

    }

    /**
     * deliver pending local messages
     * @param currentVectorClock
     */

    private void deliverPendingLocalMessages(Map<Integer, Integer> currentVectorClock) {

        Iterator<Message> holdbackQueueIter = holdbackQueue.iterator();
        while(holdbackQueueIter.hasNext()) {
            Message m = holdbackQueueIter.next();
            //TODO: don't think we need this check for readiness since we check before this function
            if (isReadyForCODelivery(m.getSourceId(), m.getVectorTimestamp(), currentVectorClock)) {
                deliverMessage(m);
                holdbackQueueIter.remove();
                incrementMap(m.getSourceId(), currentVectorClock);
            }
        }
    }

    private void pushToHoldbackQueue(Message m) { holdbackQueue.add(m); }
}
