package edu.uiuc.cs.cs425.mp1;

import com.beust.jcommander.JCommander;
import edu.uiuc.cs.cs425.mp1.config.Configuration;
import edu.uiuc.cs.cs425.mp1.config.ServerConfig;
import edu.uiuc.cs.cs425.mp1.server.Driver;
import edu.uiuc.cs.cs425.mp1.server.SequencerDriver;
import edu.uiuc.cs.cs425.mp1.server.delivery.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

            if (parserModule.isSequencer()) {
                // Launch sequencer
                logger.info("Running as sequencer node");
                new SequencerDriver(id, ip, port, new SequencerDeliverer(id)).start();
                return;
            }

            Deliverer deliverer;

            if (parserModule.getMulticastProtocol().equals("FIFO")) {
                logger.info("Using multicast protocol FIFO");
                deliverer = new FIFODeliverer();
            } else if (parserModule.getMulticastProtocol().equalsIgnoreCase("causal")) {
                logger.info("Using multicast protocol Causal Ordering");
                deliverer = new CausalDeliverer();
            } else if (parserModule.getMulticastProtocol().equalsIgnoreCase("total")) {
                logger.info("Using multicast protocol Total Ordering");
                deliverer = new TODeliverer();
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
