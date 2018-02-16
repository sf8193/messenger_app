package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.config.ServerConfig;
import edu.uiuc.cs.cs425.mp1.data.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;

public enum OperationalStore {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ClientSocketListener.class.getName());

    // Map of socket ids to clients sockets.
    private final ConcurrentHashMap<Integer, SynchronizedSocket> socketMap = new ConcurrentHashMap<>();
    // Message holdback queue.
    private final ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();


    public SynchronizedSocket getSocket(int id) {
        return socketMap.get(id);
    }

    public void loadSocketMap() {
        for(ServerConfig serverConfig : Configuration.INSTANCE.getServerConfigs()) {
            SynchronizedSocket clientSocket =
                    SynchronizedSocket.createEmptyClientSocket(
                            serverConfig.getId(), serverConfig.getIPAddress(), serverConfig.getPort());
            socketMap.put(serverConfig.getId(), clientSocket);
        }
    }

    public void pushToQueue(Message m){
        queue.add(m);
    }
    //check if there are any messages needing to be sent
    public boolean checkForMessage(){
        return queue.isEmpty();
    }


    public final Message getNextMessage(){
        if(queue.isEmpty())
            logger.error("queue is trying to be read with no messages in it");

        Message toBeSent = queue.peek();
        queue.remove();
        return toBeSent;
    }


}
