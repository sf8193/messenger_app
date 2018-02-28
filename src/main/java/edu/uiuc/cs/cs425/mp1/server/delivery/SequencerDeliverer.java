package edu.uiuc.cs.cs425.mp1.server.delivery;

import edu.uiuc.cs.cs425.mp1.data.Message;
import edu.uiuc.cs.cs425.mp1.data.MessageFactory;
import edu.uiuc.cs.cs425.mp1.server.Sender;

import java.util.HashMap;
import java.util.Map;

public class SequencerDeliverer extends Deliverer {

    private int id;
    private int localCounter = 0;
    private Map<Integer, Message> messageOrderMap = new HashMap<>();

    public SequencerDeliverer(int id) {
        this.id = id;
    }

    @Override
    protected void handleMessage(Message m) {
        if (m.isDirectMessage()) {
            String warningMessage = "Cannot send messages to the sequencer node";
            Message warning = MessageFactory.createMessage(-1, warningMessage, id, m.getSourceId(), 0, true);
            new Sender(warning)
        }
    }
}
