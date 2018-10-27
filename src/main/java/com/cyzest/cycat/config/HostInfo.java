package com.cyzest.cycat.config;

import java.util.Map;

public class HostInfo {

    public static final String DEFAULT_HOST = "localhost";

    private String host;
    private String root;
    private String index;
    private Map<String, String> errorPage;
    private Map<String, String> servletMapping;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Map<String, String> getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(Map<String, String> errorPage) {
        this.errorPage = errorPage;
    }

    public Map<String, String> getServletMapping() {
        return servletMapping;
    }

    public void setServletMapping(Map<String, String> servletMapping) {
        this.servletMapping = servletMapping;
    }
}
