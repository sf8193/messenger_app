package edu.uiuc.cs.cs425.mp1.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration singleton for entire process.
 * Maintains:
 *  - In-memory table of configuration file process entries keyed by id
 *  - Sorted list
 * All maintained data is immutable after invocation of {@link Configuration#readConfigurationFile}
 */
public enum Configuration {
    INSTANCE;

    // Map of process ids to server process configurations.
    private final Map<Integer, ServerConfig> serverConfigs = new ConcurrentHashMap<>();
    // Immutable and sorted (ascending) list of process ids.
    private volatile List<Integer> sortedIds;
    // Minimum message delay
    int minDelay;
    // Maximum message delay;
    int maxDelay;

    /**
     * Parse configuration file into memory.
     */
    public void readConfigurationFile(String configFilePath) throws IOException {
        String fullPath = Paths.get(configFilePath).toAbsolutePath().toString();
        try(BufferedReader br = new BufferedReader(new FileReader(fullPath))) {
            Gson gson = new GsonBuilder().create();
            ConfigurationFile configurationFile = gson.fromJson(br, ConfigurationFile.class);
            loadConfigurationFile(configurationFile);
        }
    }

    /**
     * Read all server configs
     */
    public Collection<ServerConfig> getServerConfigs() {
        return serverConfigs.values();
    }

    public ServerConfig getServerConfig(int id) {
        return serverConfigs.get(id);
    }

    public List<Integer> getSortedIds() { return sortedIds; }

    public int getMinDelay() {
        return minDelay;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    /**
     * load important parts of config file into memory
     * @param configFile
     */

    private void loadConfigurationFile(ConfigurationFile configFile) {

        minDelay = configFile.getMinDelay();
        maxDelay = configFile.getMaxDelay();

        List<Integer> ids = new ArrayList<>();

        for (ServerConfig serverConfig : configFile.getServerConfigs()) {
            serverConfigs.put(serverConfig.getId(), serverConfig);
            ids.add(serverConfig.getId());
        }
        Collections.sort(ids);
        sortedIds = Collections.unmodifiableList(ids);
        OperationalStore.INSTANCE.initFIFOClock();
        OperationalStore.INSTANCE.initVectorClock();
    }

    /**
     *
     * @param id
     * @return socket with specified ip and port
     * @throws IOException
     */

    public Socket createNewSocket(int id) throws IOException {
        ServerConfig config = serverConfigs.get(id);
        return new Socket(config.getIPAddress(), config.getPort());
    }
}
