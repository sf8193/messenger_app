package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.server.delivery.Deliverer;
import edu.uiuc.cs.cs425.mp1.server.delivery.SequencerDeliverer;

import java.util.Scanner;

public class SequencerDriver extends Driver {

    public SequencerDriver(int id, String ip, int port, SequencerDeliverer deliverer) {
        super(id, ip, port, deliverer);
    }

    @Override
    protected void listenForInput() {
        Scanner scanner = new Scanner(System.in);
        String line;
        final String PROMPT = "> ";
        System.out.print("PROMPT");
        while(scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("exit")) {
                System.out.println("Closing sequencer...");
                return;
            } else {
                System.out.println("Unable to recognize given command: " + line);
                System.out.print(PROMPT);
            }
        }
    }
}
