package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.data.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Deliverer implements Runnable {

    private static final Logger logger = LogManager.getLogger(Deliverer.class.getName());

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = OperationalStore.INSTANCE.blockingQueue.take();
                if (message == null) {
                    logger.warn("Received poison pill. Exiting...");
                    return;
                }
                System.out.println("Received message: " + message);
            }
        } catch (InterruptedException ex) {
            logger.warn("Thread interrupted. Exiting...", ex);
        }
    }
}
