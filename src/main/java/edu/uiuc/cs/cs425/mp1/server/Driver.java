package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.data.Message;
import edu.uiuc.cs.cs425.mp1.data.MessageFactory;
import edu.uiuc.cs.cs425.mp1.server.delivery.Deliverer;
import edu.uiuc.cs.cs425.mp1.util.ServerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main driver program for setting up sockets, processing user inputs, and spawning send threads.
 */
public class Driver {

    private static final Logger logger = LogManager.getLogger();

    private int id;
    private String ip;
    private int port;
    private Deliverer deliverer;

    private final Pattern unicastPrompt = Pattern.compile("^send ([0-9]) (.*)$");
    private final Pattern multicastPrompt = Pattern.compile("^msend (.*)$");
    public final static String PROMPT = "> ";

    public Driver(int id, String ip, int port, Deliverer deliverer) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.deliverer = deliverer;
    }

    public void start() {
        // Start server socket listener.
        Thread listener = new Thread(new ServerSocketListener(id, port));
        listener.start();

        // Start deliverer
        Thread delivererThread = new Thread(deliverer);
        delivererThread.start();

        // CONTINUE on ENTER (for waiting until all processes' listeners are running)
        promptEnterKey();

        // Create socket connections with servers with higher ids.
        makeSocketConnections();

        // Loop and process user input
        listenForInput();

        // Close threads
        OperationalStore.INSTANCE.pushToBlockingQueue(OperationalStore.INSTANCE.poisonPill);
    }

    private void makeSocketConnections() {
        List<Integer> immutableSortedProcIds = Configuration.INSTANCE.getSortedIds();
        for (int destId : ServerUtils.getTargetProcesses(id, immutableSortedProcIds)) {
            try {
                makeConnection(destId);
            } catch (IOException ioEx) {
                logger.error("Failed to make outbound connection to " + destId);
                throw new RuntimeException("Unable to make connections to all other processes", ioEx);
            }
        }
    }

    private void makeConnection(int destId) throws IOException {
        Socket newConnection = Configuration.INSTANCE.createNewSocket(destId);
        ObjectOutputStream oos = new ObjectOutputStream(newConnection.getOutputStream());
        oos.writeObject(MessageFactory.createIdentifierMessage(id));
        OperationalStore.INSTANCE.oosMap.put(destId, oos);
        OperationalStore.INSTANCE.setClientSocket(destId, newConnection);
        ServerSocketListener.createNewClientSocketListener(newConnection, destId, id);
    }

    private static void promptEnterKey() {
        System.out.println("Press 'ENTER' to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    protected void listenForInput(){
        Scanner scanner = new Scanner(System.in);
        String line;
        final String PROMPT = "> ";
        System.out.print(PROMPT);
        while (scanner.hasNextLine()){
            line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Closing node...");
                return;
            }
            if (line.startsWith("send")) {
                Matcher m = unicastPrompt.matcher(line);
                if (!m.matches()) {
                    System.out.println("Incorrect syntax, could not send message.");
                    System.out.print(PROMPT);
                    continue;
                }
                int destId = Integer.parseInt(m.group(1));
                String msg = m.group(2);
                unicastSend(msg, destId);
            } else if (line.startsWith("msend")) {
                Matcher m = multicastPrompt.matcher(line);
                if (!m.matches()) {
                    System.out.println("Incorrect syntax, could not send message.");
                    System.out.print(PROMPT);
                    continue;
                }
                String msg = m.group(1);
                multicastSend(msg);
            } else {
                System.out.println("Unable to recognize given command: " + line);
                System.out.print(PROMPT);
            }
            System.out.print(PROMPT);
        }
    }

    private void multicastSend(String msg) {
        OperationalStore.INSTANCE.incrementFIFOClock(id);
        OperationalStore.INSTANCE.incrementVectorClock(id);
        int messageId = MessageFactory.getMessageId(id);
        for (Integer destId : Configuration.INSTANCE.getSortedIds()) {
            Message m = MessageFactory.createMessage(messageId, msg, id, destId);
            unicastSendHelper(m);
        }
    }

    private void unicastSend(String msg, int destId) {
        Message m = MessageFactory.createMessage(MessageFactory.getMessageId(id), msg, id, destId,true);
        unicastSendHelper(m);
    }

    private void unicastSendHelper(Message message) {
        new Thread(new Sender(message)).start();
    }

}
