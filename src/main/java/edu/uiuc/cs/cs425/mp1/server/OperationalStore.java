package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.data.Message;
import edu.uiuc.cs.cs425.mp1.data.MessageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * shared memory class
 */
public enum OperationalStore {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ClientSocketListener.class.getName());

    // Map of socket ids to clients sockets.
    public final Map<Integer, Socket> socketMap = new ConcurrentHashMap<>();
    public final Map<Integer, ObjectOutputStream> oosMap = new ConcurrentHashMap<>();
    // Blocking queue for message arrival handling.
    public final BlockingQueue<Message> blockingQueue = new LinkedBlockingQueue<>();
    public final Message poisonPill = MessageFactory.createIdentifierMessage(0); // fixed reference for checking poison pill
    // List of client socket listeners.
    public final ConcurrentLinkedQueue<Thread> clientSocketListeners = new ConcurrentLinkedQueue<>();
    // Process clock for FIFO Multi-cast
    public final ConcurrentHashMap<Integer, Integer> fifoClock = new ConcurrentHashMap<>();
    // Vector clock for Causal Multi-cast
    public final ConcurrentHashMap<Integer, Integer> vectorClock = new ConcurrentHashMap<>();

    public void initFIFOClock() {
        for (Integer id : Configuration.INSTANCE.getSortedIds()) {
            fifoClock.put(id, 0);
        }
    }

    public void initVectorClock() {
        for (Integer id: Configuration.INSTANCE.getSortedIds()) {
            vectorClock.put(id, 0);
        }
    }

    public void incrementFIFOClock(int id) {
        fifoClock.put(id, fifoClock.get(id) + 1);
    }

    public void incrementVectorClock(int id) { vectorClock.put(id, vectorClock.get(id) + 1); }

    public void updateVectorClock(Map<Integer, Integer> updatedVectorClock) {
        vectorClock.putAll(updatedVectorClock);
    }

    public Map<Integer, Integer> getVectorClock() {
        return new HashMap<>(vectorClock);
    }

    public int getFIFOProcessMessageNumber(int id) {
        return fifoClock.get(id);
    }

    public void setClientSocket(int id, Socket newSocket) {
        Socket existingSocket = socketMap.getOrDefault(id, null);
        if (existingSocket != null) {
            try {
                existingSocket.close();
            } catch(IOException ioEx) {
                logger.error("Unable to close existing socket", ioEx);
            }
        }
        socketMap.put(id, newSocket);
    }

    public void pushToBlockingQueue(Message m) {
        logger.debug("Push following message to blocking queue: " + m);
        blockingQueue.add(m);
    }


}
