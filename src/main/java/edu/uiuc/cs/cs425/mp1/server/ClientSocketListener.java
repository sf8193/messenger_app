package edu.uiuc.cs.cs425.mp1.server;

import java.util.concurrent.Semaphore;

public class ClientSocketListener implements Runnable {

    private Semaphore semaphore;
    private SynchronizedSocket clientSocket;
    int destId;

    public ClientSocketListener(SynchronizedSocket clientSocket, int destId, Semaphore semaphore) {
        this.semaphore = semaphore;
        this.clientSocket = clientSocket;
        this.destId = destId;
    }

    /**
     * TODO(sfelde2): Implement socket listeners
     * Client socket listeners should:
     *  - Loop on the output stream
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

    }
}
