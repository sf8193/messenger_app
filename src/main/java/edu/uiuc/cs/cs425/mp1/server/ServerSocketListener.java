package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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

    private static final Logger logger = LogManager.getLogger();

    private int id;
    private int serverPort;
    protected ServerSocket serverSocket = null;
    protected Thread runningThread = null;
    private List<Thread> clientSocketListeners = new ArrayList<>();
    private final Semaphore semaphore = new Semaphore(0);
    private int threadCount = 0;

    public ServerSocketListener(int id, int port) {
        this.id = id;
        this.serverPort = port;
    }

    @Override
    public void run() {
        synchronized(this) {
            this.runningThread = Thread.currentThread();
        }

        while (!this.runningThread.isInterrupted()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                String host = clientSocket.getInetAddress().getHostName();
                int remotePort = clientSocket.getPort();
                int remoteId = Configuration.INSTANCE.getId(host, remotePort);
                SynchronizedSocket syncSocket = OperationalStore.INSTANCE.getSocket(remoteId);
                syncSocket.setConnection(clientSocket);
                Thread newClientSocketListener = new Thread(
                        new ClientSocketListener(syncSocket, remoteId, this.id, semaphore));
                clientSocketListeners.add(newClientSocketListener);
                newClientSocketListener.start();
                threadCount++;
            } catch (IOException ioEx) {
                if (this.runningThread.isInterrupted()) {
                    System.out.println("Main listener stopped. Process is no longer accepting connections.");
                    stop();
                    return;
                }
                throw new RuntimeException("Error accepting client connection", ioEx);
            } catch (NoSuchElementException ex) {
                logger.error("Unidentifiable remote process attempted to connect", ex);
                try {
                    clientSocket.close();
                } catch (IOException ioEx) {
                    logger.error("Unable to close unidentified client socket", ioEx);
                }
            }
        }

    }

    private void stop() {
        for (Thread thread : clientSocketListeners) {
            thread.interrupt();
        }
        try {
            semaphore.acquire(threadCount);
        } catch (InterruptedException intEx) {
            logger.error("Unable to wait for all socket listener threads", intEx);
        }
        try {
            this.serverSocket.close();
        } catch (IOException ioEx) {
            throw new RuntimeException("Error closing server", ioEx);
        }
    }

    public void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException ioEx) {
            throw new RuntimeException("Cannot open port " + this.serverPort, ioEx);
        }
    }

}
