package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.config.ServerConfig;
import edu.uiuc.cs.cs425.mp1.data.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;

public enum OperationalStore {
    INSTANCE;

    // Map of socket ids to clients sockets.
    private final ConcurrentHashMap<Integer, SynchronizedSocket> socketMap = new ConcurrentHashMap<>();
    // Message holdback queue.
    public final ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();

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


}
