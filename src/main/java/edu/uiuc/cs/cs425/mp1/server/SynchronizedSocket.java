package edu.uiuc.cs.cs425.mp1.server;

import java.io.*;
import java.net.Socket;

public class SynchronizedSocket {

    private volatile Socket socket;
    int destId;
    private String host;
    private int port;

    private SynchronizedSocket(Socket socket, int destId, String host, int port) {
        this.socket = socket;
        this.destId = destId;
        this.host = host;
        this.port = port;
    }

    public static SynchronizedSocket createEmptyClientSocket(int destId, String host, int port) {
        return new SynchronizedSocket(null, destId, host, port);
    }

    // Status methods

    public synchronized boolean isEmpty() {
        return empty();
    }

    private boolean empty() { return socket == null; }

    public synchronized boolean isClosed() {
        return isEmpty() || socket.isClosed();
    }

    /**
     * Dangerous access to underlying resource.
     * All access should be done through common synchronized methods.
     */
    public Socket getSocket() {
        return socket;
    }

    // Synchronized getter methods for output stream / input stream methods

    public synchronized InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public synchronized OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    // Connection methods

    /**
     * Sets internal socket to provided socket if currently {@link SynchronizedSocket#isEmpty()}.
     * @return true if sets, otherwise false (if noop)
     */
    public synchronized boolean setConnection(Socket clientSocket) {
        if (empty()) {
            socket = clientSocket;
            return true;
        }
        return false;
    }

    public synchronized void forceSetConnection(Socket clientSocket) throws IOException {
        if(!empty()) {
            socket.close();
        }
        socket = clientSocket;
    }

    /**
     * Synchronized wrapper around socket create.
     * Creates a new socket connection.
     * @return Returns true if connects, and false if socket is already connected.
     */
    public synchronized boolean connect() throws IOException {
        if (empty()) {
            System.out.println("Connecting to id: "+ destId);
            socket = new Socket(host, port);
            return true;
        }
        return false;
    }

    /**
     * Force closes existing socket, and connects to new host and port.
     */
    public synchronized void forceConnectSocket() throws IOException {
        if (!empty()) {
            socket.close();
        }
        socket = new Socket(host, port);
    }

    public synchronized void close() throws IOException {
        socket.close();
        socket = null;
    }

}
