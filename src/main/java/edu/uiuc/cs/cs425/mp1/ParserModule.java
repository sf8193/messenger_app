package edu.uiuc.cs.cs425.mp1;

import com.beust.jcommander.Parameter;

/**
 * Parsing options (using JCommander) for {@link DriverMain}
 */
public class ParserModule {

    @Parameter(names = "--id", description = "unique integer id of process in distributed system", required = true)
    private Integer id;

    @Parameter(names = "--config_path", description = "Path to distributed system configuration file", required = true)
    private String configPath;

    @Parameter(names = "--sequencer", description = "Flag indicating process will function as sequencer node")
    private boolean sequencer;

    @Parameter(names = "--multicast", description = "Multicast protocol to use, defaults to none")
    private String multicastProtocol = "";

    public Integer getId() {
        return id;
    }

    public boolean isSequencer() {
        return sequencer;
    }

    public String getConfigPath() {
        return configPath;
    }

    public String getMulticastProtocol() {
        return multicastProtocol;
    }
}
