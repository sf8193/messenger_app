package edu.uiuc.cs.cs425.mp1.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import edu.uiuc.cs.cs425.mp1.data.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientSocketListener implements Runnable {

    private static final Logger logger = LogManager.getLogger(ClientSocketListener.class.getName());

    private Semaphore semaphore;
    private SynchronizedSocket clientSocket;
    private int myId;
    private int destId;
    private Thread runningThread = null;


    public ClientSocketListener(SynchronizedSocket clientSocket, int destId, int myId, Semaphore semaphore) {
        this.semaphore = semaphore;
        this.clientSocket = clientSocket;
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

        synchronized(this) {
            this.runningThread = Thread.currentThread();
        }

        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            String msg = "Could not acquire permit for reading message";
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            Message incomingMessage;
            // Grab messages coming in through stream and push to buffer to be read.
            while((incomingMessage = (Message) ois.readObject()) != null){
                OperationalStore.INSTANCE.pushToBlockingQueue(incomingMessage);
            }
        } catch (IOException ioEx ){
            // if thread is interrupted while reading
            if(this.runningThread.isInterrupted()){
                logger.warn("Thread was interrupted");
                stop();
                return;
            } else {
                // Logic for re-opening connection.
                /*
                    To prevent both processes from simultaneously attempting to re-connect, we universally decide that
                    the lower-numbered process is required for re-connecting. The higher-numbered process should
                    simply exit and establish the new connection.
                 */
                if (myId < destId) {
                    reconnectSocket();
                } else {
                    // if (myId > destId): Close cleanly, and accept new connection
                    logger.warn("Thread was closed due to broken socket", ioEx);
                    stop();
                    return;
                }
            }
        } catch (ClassNotFoundException classEx) {

            String msg = "Unable to read object from input stream";
            logger.error(msg, classEx);
            throw new RuntimeException(msg, classEx);
        }
        stop();
    }

    /**
     * Reconnect to destination process.
     */
    private void reconnectSocket() {
        // Hacky - plz remove.
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // Re-connect.
        try {
            OperationalStore.INSTANCE.getSocket(destId).connect();
        } catch (IOException ioEx) {
            String msg = "Unable to re-connect to process " + destId;
            logger.error(msg, ioEx);
            throw new RuntimeException(msg + " yer fucked", ioEx);
        }
    }

    /**
     * Clean exit
     *  - Release semaphore.
     *  - Close socket.
     */
    private void stop() {

        // Release parent thread's semaphore.
        this.semaphore.release();

        // Close socket.
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.error("could not close client socket properly", e);
        }
    }
}
