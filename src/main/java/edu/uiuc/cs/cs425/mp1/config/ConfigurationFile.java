package edu.uiuc.cs.cs425.mp1.config;

import com.google.gson.annotations.*;

/**
 * GSON model class for configuration file.
 */
public class ConfigurationFile {

    // List of server configurations.
    @SerializedName("server_configurations")
    private ServerConfig[] serverConfigs;

    // Configuration of single sequencer server.
    // @SerializedName("sequencer_configuration")
    // Note: Serialization disabled until sequencer implementation.
    private transient ServerConfig sequencerConfig = null;

    public ServerConfig[] getServerConfigs() {
        return serverConfigs;
    }

    public ServerConfig getSequencerConfig() {
        return sequencerConfig;
    }

}
