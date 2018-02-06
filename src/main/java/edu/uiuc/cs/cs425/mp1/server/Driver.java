package edu.uiuc.cs.cs425.mp1.server;

import edu.uiuc.cs.cs425.mp1.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Driver {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            Configuration.INSTANCE.readConfigurationFile("/Users/arvind/Documents/College_2015/current_classes/cs425/mp1/configuration_file.json");
        } catch(Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }

}
