package com.cyzest.cycat.config;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ServerConfigurer.class);

    private static final Gson gson = new Gson();

    public static ServerConfig createDefaultServerConfig() {

        ServerConfig serverConfig = new ServerConfig();

        HostInfo host = new HostInfo();

        host.setHost("localhost");
        host.setIndex("index.html");

        Map<String, String> servletMapping = new HashMap<>();
        servletMapping.put("/test", "com.cyzest.cycat.service.CurrentTimeServlet");

        host.setServletMapping(servletMapping);

        serverConfig.setPort(8080);
        serverConfig.setThread(10);
        serverConfig.setHosts(Collections.singletonList(host));

        return serverConfig;
    }

    public static ServerConfig createServerConfig(File configFile) throws Exception {

        ServerConfig serverConfig;

        try (Reader targetReader = new FileReader(configFile)) {

            serverConfig = gson.fromJson(targetReader, ServerConfig.class);

            Integer port = serverConfig.getPort();

            if (port == null) {
                serverConfig.setPort(8080);
            }

        } catch (Exception ex) {
            logger.error("illegal access configFile", ex);
            throw ex;
        }

        return serverConfig;
    }



}
