package edu.uiuc.cs.cs425.mp1;

import com.beust.jcommander.JCommander;
import edu.uiuc.cs.cs425.mp1.config.Configuration;

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

}
