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

    @SerializedName("min_delay")
    private long minDelay;

    @SerializedName("max_delay")
    private long maxDelay;

    public ServerConfig[] getServerConfigs() {
        return serverConfigs;
    }

    public ServerConfig getSequencerConfig() {
        return sequencerConfig;
    }

    public long getMinDelay() {
        return minDelay;
    }

    public long getMaxDelay() {
        return maxDelay;
    }
}
