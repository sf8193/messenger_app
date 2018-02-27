package edu.uiuc.cs.cs425.mp1.server.delivery;

import edu.uiuc.cs.cs425.mp1.data.Message;
import edu.uiuc.cs.cs425.mp1.server.Driver;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Interface defining
 */
public abstract class Deliverer implements Runnable {

    private static final Logger logger = LogManager.getLogger(BasicDeliverer.class.getName());

    @Override
    public void run() {
        try {
            logger.debug("Start listening");
            while (!Thread.currentThread().isInterrupted()) {
                Message message = OperationalStore.INSTANCE.blockingQueue.take();
                if (message == OperationalStore.INSTANCE.poisonPill) {
                    logger.warn("Received poison pill. Exiting...");
                    return;
                }
                handleMessage(message);
            }
            logger.debug("End listening");
        } catch (InterruptedException ex) {
            logger.warn("Thread interrupted. Exiting...", ex);
        }
    }

    protected abstract void handleMessage(Message m);

    protected void deliverMessage(Message m) {
        System.out.println("Received message: " + m);
        System.out.print(Driver.PROMPT);
    }
}
