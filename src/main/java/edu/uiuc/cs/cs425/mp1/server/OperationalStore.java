package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.config.ServerConfig;
import edu.uiuc.cs.cs425.mp1.data.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public enum OperationalStore {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ClientSocketListener.class.getName());

    // Map of socket ids to clients sockets.
    private volatile Map<Integer, SynchronizedSocket> socketMap = new ConcurrentHashMap<>();
    // Message holdback queue.
    private final ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
    // Blocking queue for message arrival handling.
    public final BlockingQueue<Message> blockingQueue = new LinkedBlockingQueue<>();

    public SynchronizedSocket getSocket(int id) {
        return socketMap.get(id);
    }

    public void connect(int id) throws IOException {
        SynchronizedSocket socket = socketMap.get(id);
        socket.connect();
        socketMap.put(id, socket);
    }

    public void loadSocketMap() {
        for(ServerConfig serverConfig : Configuration.INSTANCE.getServerConfigs()) {
            SynchronizedSocket clientSocket =
                    SynchronizedSocket.createEmptyClientSocket(
                            serverConfig.getId(), serverConfig.getIPAddress(), serverConfig.getPort());
            socketMap.put(serverConfig.getId(), clientSocket);
        }
    }

    public void pushToBlockingQueue(Message m) {
        blockingQueue.add(m);
    }

}
