package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.util.ServerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Main driver program for setting up sockets, processing user inputs, and spawning send threads.
 */
public class Driver {

    private static final Logger logger = LogManager.getLogger();

    private int id;
    private String ip;
    private int port;
    private Thread listener;

    private static final int CONNECTION_RETRY_LIMIT = 3;
    private static final long RETRY_TIMER_MILLIS = 1000;

    public Driver(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public void start() {
        // Start server socket listener.
        listener = new Thread(new ServerSocketListener(id, port));
        listener.start();

        // CONTINUE on ENTER (for waiting until all processes' listeners are running)
        promptEnterKey();

        // Create socket connections with servers with higher ids.
        makeSocketConnections();

        // Loop and process user input
        processUserInput();

    }

    public void makeSocketConnections() {
        List<Integer> immutableSortedProcIds = Configuration.INSTANCE.getSortedIds();
        for (int id : ServerUtils.getTargetProcesses(id, immutableSortedProcIds)) {
            // TODO(avjykmr2): Fix the disgusting syntax below.
            boolean successfulConnection = tryWithRetries(new ConnectionAttempt() {
                @Override
                public void connect() throws IOException {
                    OperationalStore.INSTANCE.getSocket(id).connect();
                }

                @Override
                public int id() {
                    return id;
                }
            });
            if (!successfulConnection) {
                throw new RuntimeException("Unable to make connections to all other processes");
            }
        }
    }

    public boolean tryWithRetries(ConnectionAttempt connectionAttempt) {
        int i = 0;
        for(; i < CONNECTION_RETRY_LIMIT; i++) {
            try {
                connectionAttempt.connect();
                return true;
            } catch (IOException ioEx) {
                String msg = String.format("Attempt %d: Unable to make connection with %d", i, connectionAttempt.id());
                logger.error(msg, ioEx);
                try { Thread.sleep(RETRY_TIMER_MILLIS); } catch (InterruptedException ex) {}
            }
        }
        return i != CONNECTION_RETRY_LIMIT;
    }

    interface ConnectionAttempt {
        void connect() throws IOException;
        int id();
    }

    public static void promptEnterKey() {
        System.out.println("Press 'ENTER' to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    /**
     * Loop and process user input for 'send' and 'recieve' commands.
     * TODO(sfelde2)
     */
    public void processUserInput() {

    }

}
