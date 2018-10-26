package com.cyzest.cycat.config;

import java.util.List;

public class ServerConfig {

    private Integer port;
    private Integer thread;
    private List<HostInfo> hosts;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public List<HostInfo> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostInfo> hosts) {
        this.hosts = hosts;
    }
}
