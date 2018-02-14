package edu.uiuc.cs.cs425.mp1.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.uiuc.cs.cs425.mp1.server.OperationalStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration singleton for entire process.
 * Maintains:
 *  - In-memory table of configuration file process entries keyed by id
 */
public enum Configuration {
    INSTANCE;

    // Map of process ids to server process configurations.
    private final ConcurrentHashMap<Integer, ServerConfig> serverConfigs = new ConcurrentHashMap<>();
    // Immutable and sorted (ascending) list of process ids.
    private volatile List<Integer> sortedIds;
    // Configuration for sequencer process.
    private volatile ServerConfig sequencerConfig;

    public void readConfigurationFile(String configFilePath) throws IOException {
        String fullPath = Paths.get(configFilePath).toAbsolutePath().toString();
        try(BufferedReader br = new BufferedReader(new FileReader(fullPath))) {
            Gson gson = new GsonBuilder().create();
            ConfigurationFile configurationFile = gson.fromJson(br, ConfigurationFile.class);
            loadConfigurationFile(configurationFile);
        }
        OperationalStore.INSTANCE.loadSocketMap();
    }

    public Collection<ServerConfig> getServerConfigs() {
        return serverConfigs.values();
    }

    public ServerConfig getServerConfig(int id) {
        return serverConfigs.get(id);
    }

    public List<Integer> getSortedIds() { return sortedIds; }

    public int getId(String host, int port) {
        for (Map.Entry<Integer, ServerConfig> entry: serverConfigs.entrySet()) {
            ServerConfig serverConfig = entry.getValue();
            if (serverConfig.getIPAddress().equals(host) && serverConfig.getPort() == port) {
                return entry.getKey();
            }
        }
        String msg = String.format("No such ip and port entry exists in the configuration: (%s,%d)", host, port);
        throw new NoSuchElementException(msg);
    }

    private void loadConfigurationFile(ConfigurationFile configFile) {

        List<Integer> ids = new ArrayList<>();

        for (ServerConfig serverConfig : configFile.getServerConfigs()) {
            serverConfigs.put(serverConfig.getId(), serverConfig);
            ids.add(serverConfig.getId());
        }
        Collections.sort(ids);
        sortedIds = Collections.unmodifiableList(ids);
        sequencerConfig = configFile.getSequencerConfig();
    }

}
