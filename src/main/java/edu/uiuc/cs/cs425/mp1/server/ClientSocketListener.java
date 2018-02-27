package edu.uiuc.cs.cs425.mp1.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.data.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientSocketListener implements Runnable {

    private static final Logger logger = LogManager.getLogger(ClientSocketListener.class.getName());

    private Socket clientSocket;
    private ObjectInputStream ois;
    private int myId;
    private int destId;


    public ClientSocketListener(Socket clientSocket, ObjectInputStream ois, int destId, int myId) {
        this.clientSocket = clientSocket;
        this.ois = ois;
        this.destId = destId;
        this.myId = myId;
    }

    /**
     * Client socket listeners should:
     *  - Loop on the input stream
     *  - Process messages using an ObjectInputStream with {@link edu.uiuc.cs.cs425.mp1.data.Message}
     *  - Robustly handle failures ie.
     *      - If there is an exception (eg. due to broken stream/closed socket) then:
     *          - If the Thread interrupted flag has been thrown, cleanly exit
     *          - If not, then if the socket was broken/closed
     *              - If myId < destId, reopen the connection.
     *              - If myId > destId, cleanly exit.
     *  - Receive messages and push to the message queue.
     *
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message incomingMessage;
                // Grab messages coming in through stream and push to buffer to be read.
                while((incomingMessage = (Message) ois.readObject()) != null) {
                    OperationalStore.INSTANCE.pushToBlockingQueue(incomingMessage);
                }

            } catch (IOException ioEx) {
                // if thread is interrupted while reading
                if(Thread.currentThread().isInterrupted()){
                    logger.warn("Thread was interrupted");
                    stop();
                    return;
                } else {
                    // if (myId > destId): Close cleanly, and accept new connection
                    logger.warn("Thread was closed due to broken socket", ioEx);
                    stop();
                    return;
                }
            } catch (ClassNotFoundException classEx) {
                String msg = "Unable to read object from input stream";
                logger.error(msg, classEx);
                throw new RuntimeException(msg, classEx);
            }
        }
        stop();
    }

    /**
     * Clean exit
     *  - Release semaphore.
     *  - Close socket.
     */
    private void stop() {

        // Release parent thread's semaphore.
//        this.semaphore.release();

        // Close socket.
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.error("could not close client socket properly", e);
        }
    }
}
