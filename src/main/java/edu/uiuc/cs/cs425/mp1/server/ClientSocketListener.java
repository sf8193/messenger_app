package edu.uiuc.cs.cs425.mp1.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.Semaphore;
import edu.uiuc.cs.cs425.mp1.data.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientSocketListener implements Runnable {

    private static final Logger logger = LogManager.getLogger(ClientSocketListener.class.getName());


    private Semaphore semaphore;
    private SynchronizedSocket clientSocket;
    int destId;
    int myId;
    private Thread runningThread = null;


    public ClientSocketListener(SynchronizedSocket clientSocket, int destId, int myId, Semaphore semaphore) {
        this.semaphore = semaphore;
        this.clientSocket = clientSocket;
        this.destId = destId;
        this.myId = myId;
    }

    /**
     * TODO(sfelde2): Implement socket listeners
     * Client socket listeners should:
     *  - Loop on the input stream
     *  - Process messages using an ObjectInputStream with {@link edu.uiuc.cs.cs425.mp1.data.Message}
     *  - Robustly handle failures ie.
     *      - If there is an exception (eg. due to broken stream/closed socket) then:
     *          - If the Thread interrupted flag has been thrown, cleanly exit
     *          - If not, then if the socket was interrupted
     *  - Receive messages and push to the message queue.
     *
     *  Clean exit should:
     *   - Close socket
     *   - Release semaphore
     *   - etc.
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
            logger.error("could not acquire permit for reading message", e);
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream();

            Message incomingMessage;
            //grab messages coming in through stream and push to buffer to be read
            while( incomingMessage = (Message) ois.readObject()){
                OperationalStore.INSTANCE.pushToQueue(incomingMessage);
            }
        }
        catch(IOException IOex ){
            // if thread is interrupted while reading
            if(this.runningThread.isInterrupted()){
                System.out.println("Thread was interrupted");
                stop();
                return;
            }else
            //this is for the objectinputstream check --- not sure if this check is necesarry depending on java internals
            throw new RuntimeException("No input stream to receive");
        }
        stop();
    }
    //clean exit
    private void stop() {

        //release semaphore
        try {
            this.semaphore.release();
        } catch (InterruptedException intEx) {
            logger.error("Could not release permit", intEx);
        }
        //close socket
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.error("could not close client socket properly", e);
        }
    }
}
