package edu.uiuc.cs.cs425.mp1.server.delivery;

import edu.uiuc.cs.cs425.mp1.data.Message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * class that handles Causally ordered messages
 */
public class TODeliverer extends Deliverer {

    private TOHoldbackQueue holdbackQueue = new TOHoldbackQueue();
    private int localCounter = 0;

    @Override
    public void handleMessage(Message m) {
        if (m.isDirectMessage()) {
            deliverMessage(m);
            return;
        }
        // Sequencer message
        if (m.isSequencerMessage()) {
            holdbackQueue.pushOrdering(m);
            // Does holdback queue contain matching message
            if (holdbackQueue.containsMessage(m.getMessageId())) {
                deliverPendingLocalMessages();
            }
        } else { // Regular message
            holdbackQueue.addMessage(m);
            //if message is top of priority queue send
            if (!holdbackQueue.noOrderings() && m.getMessageId() == holdbackQueue.peekOrdering().getMessageId()) {
                deliverPendingLocalMessages();
            }
        }
    }

    private void deliverPendingLocalMessages() {
        while (!holdbackQueue.noOrderings()) {
            Message ordering = holdbackQueue.peekOrdering();
            if (holdbackQueue.containsMessage(ordering.getMessageId())) {
                if (ordering.getSequencerOrder() == localCounter) {
                    Message content = holdbackQueue.getMessage(ordering.getMessageId());
                    deliverMessage(content);
                    localCounter++;
                    holdbackQueue.popOrdering();
                    continue;
                }
            }
            return;
        }
    }
}
