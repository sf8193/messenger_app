package edu.uiuc.cs.cs425.mp1.config;

import com.google.gson.annotations.SerializedName;

import java.net.Socket;

/**
 * GSON Model class for each server config entry.
 */
public class ServerConfig {

    // Unique integer id to refer to server.
    @SerializedName("id")
    private int id;

    // Sever ip address.
    @SerializedName("ip_address")
    private String IPAddress;

    // Server port.
    @SerializedName("port")
    private int port;

    public ServerConfig() {}

    public ServerConfig(ServerConfig other) {
        id = other.id;
        IPAddress = other.IPAddress;
        port = other.port;
    }

    public int getId() {
        return id;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public int getPort() {
        return port;
    }

}
