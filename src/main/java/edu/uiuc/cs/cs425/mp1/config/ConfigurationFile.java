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
     @SerializedName("sequencer_configuration")
    private ServerConfig sequencerConfig = null;

    @SerializedName("network_delay_min")
    private int networkDelayMin;

    @SerializedName("network_delay_max")
    private int networkDelayMax;

    public ServerConfig[] getServerConfigs() {
        return serverConfigs;
    }

    public ServerConfig getSequencerConfig() {
        return sequencerConfig;
    }


}
