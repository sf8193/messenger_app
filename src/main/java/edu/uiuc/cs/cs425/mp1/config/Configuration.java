package edu.uiuc.cs.cs425.mp1.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Configuration {
    INSTANCE;

    private Map<Integer, ServerConfig> serverConfigs;

    private ServerConfig sequencerConfig;

    public void readConfigurationFile(String configFilePath) throws IOException {
        String fullPath = Paths.get(configFilePath).toAbsolutePath().toString();
        try(BufferedReader br = new BufferedReader(new FileReader(fullPath))) {
            Gson gson = new GsonBuilder().create();
            ConfigurationFile configurationFile = gson.fromJson(br, ConfigurationFile.class);
            loadConfigurationFile(configurationFile);
        }
    }

    public Map<Integer, ServerConfig> getServerConfigs() {
        return serverConfigs;
    }

    private void loadConfigurationFile(ConfigurationFile configFile) {
        serverConfigs = Arrays
                .stream(configFile.getServerConfigs())
                .collect(Collectors.toMap(ServerConfig::getId, Function.identity()));
        sequencerConfig = configFile.getSequencerConfig();
    }

}
