package edu.uiuc.cs.cs425.mp1.config;

import com.google.gson.annotations.*;

/**
 * GSON model class for configuration file.
 */
public class ConfigurationFile {

    // List of server configurations.
    @SerializedName("server_configurations")
    private ServerConfig[] serverConfigs;

    @SerializedName("min_delay")
    private int minDelay;

    @SerializedName("max_delay")
    private int maxDelay;

    public ServerConfig[] getServerConfigs() {
        return serverConfigs;
    }

    public int getMinDelay() {
        return minDelay;
    }

    public int getMaxDelay() {
        return maxDelay;
    }
}
