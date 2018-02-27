package edu.uiuc.cs.cs425.mp1.server.delivery;

import edu.uiuc.cs.cs425.mp1.data.Message;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BasicDeliverer extends Deliverer {

    private static final Logger logger = LogManager.getLogger(BasicDeliverer.class.getName());

    @Override
    protected void handleMessage(Message m) {
        deliverMessage(m);
    }
}
