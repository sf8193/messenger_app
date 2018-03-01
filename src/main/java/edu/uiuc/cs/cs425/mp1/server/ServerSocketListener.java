package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.data.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Process server socket listener. Accepts new client socket connections in new threads (non-blocking).
 * TODO(avjykmr2): Add more precise tracking of which procs are connected, possibly in OperationalStore.
 * TODO(avjykmr2): Consider moving semaphore to {@link OperationalStore}
 */
public class ServerSocketListener implements Runnable {

    // Logger using log4j2.
    private static final Logger logger = LogManager.getLogger(ServerSocketListener.class.getName());

    // Process id.
    private int id;
    // Port for server bind.
    private int serverPort;
    // Server socket.
    private ServerSocket serverSocket = null;
    // Semaphore to await clean exit of all client socket listeners.
//    private final Semaphore semaphore = new Semaphore(0);

    public ServerSocketListener(int id, int port) {
        this.id = id;
        this.serverPort = port;
    }

    /**
     * Main server run-loop for accepting client connections.
     * Accepted connections are handled in a new thread.
     * Accepted connections may be immediately rejected if:
     *  - The accepted connection
     */
    @Override
    public void run() {
        openServerSocket();
        while (!Thread.currentThread().isInterrupted()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                Message identifier = (Message) ois.readObject();
                int remoteId = identifier.getSourceId();
                //TODO: do we need this since we map them upon making a connection?
                if (!OperationalStore.INSTANCE.oosMap.containsKey(remoteId)) {
                    ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    OperationalStore.INSTANCE.oosMap.put(remoteId, oos);
                }
                OperationalStore.INSTANCE.setClientSocket(remoteId, clientSocket);
                Thread newClientSocketListener = new Thread(
                        new ClientSocketListener(clientSocket, ois, remoteId, this.id));
                OperationalStore.INSTANCE.clientSocketListeners.add(newClientSocketListener);
                newClientSocketListener.start();
            } catch (IOException ioEx) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Main listener stopped. Process is no longer accepting connections.");
                    closeServerSocket();
                    return;
                }
                throw new RuntimeException("Error accepting client connection", ioEx);
            } catch (ClassNotFoundException e) {
                logger.error("Unidentifiable remote process attempted to connect", e);
                try {
                    clientSocket.close();
                } catch (IOException ioEx) {
                    logger.error("Unable to close unidentified client socket", ioEx);
                }
            }
        }

    }

    public static void createNewClientSocketListener(Socket clientSocket, int remoteId, int sourceId) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        Thread newClientSocketListener = new Thread(
                new ClientSocketListener(clientSocket, ois, remoteId, sourceId));
        OperationalStore.INSTANCE.clientSocketListeners.add(newClientSocketListener);
        newClientSocketListener.start();
    }

    public void closeServerSocket() {
        int threadCount = OperationalStore.INSTANCE.clientSocketListeners.size();
        for (Thread thread : OperationalStore.INSTANCE.clientSocketListeners) {
            thread.interrupt();
        }

        for (Map.Entry<Integer, Socket> entry : OperationalStore.INSTANCE.socketMap.entrySet()) {
            int processId = entry.getKey();
            Socket clientSocket = entry.getValue();
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.warn("Unable to close socket for processId: " + processId, e);
            }
        }
/*        try {
            semaphore.acquire(threadCount);
        } catch (InterruptedException intEx) {
            logger.error("Unable to wait for all socket listener threads", intEx);
        }*/

        try {
            this.serverSocket.close();
        } catch (IOException ioEx) {
            throw new RuntimeException("Error closing server", ioEx);
        }
    }

    public void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            logger.debug("Current port is: " + this.serverSocket.getLocalPort());
        } catch (IOException ioEx) {
            throw new RuntimeException("Cannot open port " + this.serverPort, ioEx);
        }
    }

}
