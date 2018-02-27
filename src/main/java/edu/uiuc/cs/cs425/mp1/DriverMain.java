package edu.uiuc.cs.cs425.mp1;

import com.beust.jcommander.JCommander;
import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.config.ServerConfig;
import edu.uiuc.cs.cs425.mp1.server.Driver;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;
import edu.uiuc.cs.cs425.mp1.server.delivery.BasicDeliverer;
import edu.uiuc.cs.cs425.mp1.server.delivery.Deliverer;
import edu.uiuc.cs.cs425.mp1.server.delivery.FIFODeliverer;
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

            int id = parserModule.getId();
            ServerConfig config = Configuration.INSTANCE.getServerConfig(id);
            String ip = config.getIPAddress();
            int port = config.getPort();

            Deliverer deliverer;

            if (parserModule.getMulticastProtocol().equals("FIFO")) {
                logger.info("Using multicast protocol FIFO");
                deliverer = new FIFODeliverer();
            } else {
                deliverer = new BasicDeliverer();
            }


            new Driver(id, ip, port, deliverer).start();
        } catch(Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }

}
