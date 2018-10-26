package com.cyzest.cycat.config;

import java.util.List;

public class ServerConfig {

    private Integer port;
    private Integer thread;
    private List<HostInfo> hosts;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public List<HostInfo> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostInfo> hosts) {
        this.hosts = hosts;
    }
}
