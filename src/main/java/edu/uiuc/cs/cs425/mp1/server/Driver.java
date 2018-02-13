package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.data.Message;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Driver {

    private ConcurrentLinkedQueue<Message> messageQueue;
    private int id;
    private String ip;
    private int port;

    public Driver(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        messageQueue = new ConcurrentLinkedQueue<>();
    }

    public void start() {
        // Start listener

        // Create socket connections with servers with higher ids.

        //
    }

    public void takeCommands() {

    }
}
