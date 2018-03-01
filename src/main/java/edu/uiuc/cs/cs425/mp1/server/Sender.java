package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.data.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * helper thread that sends message by figuring out who needs to get the message from shared memory
 */
public class Sender implements Runnable {

    private static final Logger logger = LogManager.getLogger(ClientSocketListener.class.getName());

    private Message message;

    public Sender(Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        // Delay for given interval.
        try {
            Thread.sleep(message.getNetworkDelay());
        } catch (InterruptedException e) {
            logger.error("Unable to delay for full duration", e);
        }

        try {
            if (message.getSourceId() == message.getDestId()) {
                System.out.println("Sent message: " + message);
                OperationalStore.INSTANCE.pushToBlockingQueue(message);
            } else {
                ObjectOutputStream oos = OperationalStore.INSTANCE.oosMap.get(message.getDestId());
                synchronized (oos) {
                    oos.writeObject(message);
                    System.out.println("Sent message: " + message);
                }
            }
        } catch (IOException ioEx) {
            System.out.println();
            System.out.printf("Message failed to send. Contents below: \n%s\n", message.toString());
        }
    }
}
