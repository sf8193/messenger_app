package edu.uiuc.cs.cs425.mp1;

import com.beust.jcommander.JCommander;
import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 *  Launches main process driver program.
 */
public class DriverMain {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            // Parse command line arguments.
            ParserModule parserModule = new ParserModule();
            JCommander jCommander = JCommander.newBuilder().addObject(parserModule).build();
            jCommander.setProgramName("ProcessDriver");
            jCommander.parse(args);

            // Read configuration. -- "/Users/arvind/Documents/College_2015/current_classes/cs425/mp1/configuration_file.json"
            Configuration.INSTANCE.readConfigurationFile(parserModule.getConfigPath());

        } catch(Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }
    public static void promptEnterKey() {
        System.out.println("Press 'ENTER' to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    /*
    TODO: unicastSEND, unicastRecieve functions
    Listens to input on each machine from command line and checks if messages need to be received for specific machine
     */
    public static void listenForInput(){

        Scanner scanner = new Scanner(System.in);
        String input;
        while(true){
            if(input = scanner.nextLine()){
                unicastSend(input);
            }
            if(OperationalStore.INSTANCE.checkForMessage()){
                unicastReceive();
            }
        }
    }
}
