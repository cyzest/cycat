package com.cyzest.cycat.config;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ServerConfigurer.class);

    private static final Gson gson = new Gson();

    public static ServerConfig createDefaultServerConfig() {

        ServerConfig serverConfig = new ServerConfig();

        serverConfig.setPort(8080);
        serverConfig.setThread(10);
        serverConfig.setHosts(Collections.singletonList(defaultHostInfo()));

        return serverConfig;
    }

    private static HostInfo defaultHostInfo() {

        HostInfo host = new HostInfo();

        host.setHost("localhost");
        host.setRoot("html");
        host.setIndex("index.html");

        Map<String, String> errorPage = new HashMap<>();
        errorPage.put("403", "403.html");
        errorPage.put("404", "404.html");
        errorPage.put("500", "500.html");

        host.setErrorPage(errorPage);

        Map<String, String> servletMapping = new HashMap<>();
        servletMapping.put("/test", "com.cyzest.cycat.service.CurrentTimeServlet");

        host.setServletMapping(servletMapping);

        return host;
    }

    public static ServerConfig createServerConfig(File configFile) throws Exception {

        ServerConfig serverConfig;

        try (Reader targetReader = new FileReader(configFile)) {

            serverConfig = gson.fromJson(targetReader, ServerConfig.class);

            Integer port = serverConfig.getPort();

            if (port == null || port < 0 || port > 65535) {
                serverConfig.setPort(8080);
            }

            Integer thread = serverConfig.getThread();

            if (thread == null) {
                serverConfig.setThread(10);
            }

            List<HostInfo> hostInfos = serverConfig.getHosts();

            if (hostInfos != null && !hostInfos.isEmpty()) {

                for (HostInfo hostInfo : hostInfos) {
                    String documentRoot = hostInfo.getRoot();
                    if (documentRoot == null || documentRoot.isEmpty()) {
                        hostInfo.setRoot(".");
                    }
                }

                List<String> hosts = hostInfos.stream().map(HostInfo::getHost).collect(Collectors.toList());

                if (!hosts.contains("localhost")) {
                    hostInfos.add(defaultHostInfo());
                }

            } else {
                serverConfig.setHosts(Collections.singletonList(defaultHostInfo()));
            }

        } catch (Exception ex) {
            logger.error("illegal access configFile", ex);
            throw ex;
        }

        return serverConfig;
    }

}
