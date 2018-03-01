package edu.uiuc.cs.cs425.mp1.server.delivery;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.data.Message;
import edu.uiuc.cs.cs425.mp1.data.MessageFactory;
import edu.uiuc.cs.cs425.mp1.server.Sender;

import java.util.HashMap;
import java.util.Map;

public class SequencerDeliverer extends Deliverer {

    private int id;
    private int localCounter = 0;
    //TODO: why do we need this local counter?
    //private Map<Integer, Integer> messageOrderMap = new HashMap<>();
    private final String SEQUENCER_MSG_STRING = "SEQUENCER MSG";

    public SequencerDeliverer(int id) {
        this.id = id;
    }

    @Override
    protected void handleMessage(Message m) {
        if (m.isDirectMessage()) {
            String warningMessage = "Cannot send messages to the sequencer node";
            Message warning = MessageFactory.createMessage(MessageFactory.getMessageId(id), warningMessage, id,
                    m.getSourceId(), true);
            new Sender(warning).run();
            return;
        }
        /*if (messageOrderMap.containsKey(m.getMessageId())) {
            multicastSequencerMessages(m.getMessageId());
        } else {
            messageOrderMap.put(m.getMessageId(), ++localCounter);
            multicastSequencerMessages();
        }*/
        multicastSequencerMessages(m.getMessageId());
        localCounter++;
    }

    /**
     * send multicast message to everyone
     * @param messageId
     */
    private void multicastSequencerMessages(int messageId) {
        for (Integer destId : Configuration.INSTANCE.getSortedIds()) {
            if (id != destId) {
                sendSequencerMessage(messageId, destId);
            }
        }
    }

    private void sendSequencerMessage(int messageId, int destId) {
        Message sequenceMessage = MessageFactory.createSequencerMessage(
                messageId, SEQUENCER_MSG_STRING, id, destId, localCounter);
        new Sender(sequenceMessage).run();
    }
}
