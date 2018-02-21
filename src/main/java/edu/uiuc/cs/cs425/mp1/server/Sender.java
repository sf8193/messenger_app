package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.data.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;

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
            SynchronizedSocket socket = OperationalStore.INSTANCE.getSocket(message.getDestId());
            logger.debug("Socket is empty: " + socket.isEmpty());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            System.out.println("Sent message: " + message);
        } catch (IOException ioEx) {
            System.out.println();
            System.out.printf("Message failed to send. Contents below: \n%s\n", message.toString());
        }
    }
}
