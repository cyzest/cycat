package com.cyzest.cycat;

import com.cyzest.cycat.config.ServerConfig;
import com.cyzest.cycat.config.ServerConfigurer;
import com.cyzest.cycat.handler.ConnectionHandler;
import com.cyzest.cycat.handler.HttpRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CycatServer {

    private static final Logger logger = LoggerFactory.getLogger(CycatServer.class);

    private ServerConfig serverConfig;

    private CycatServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    private void start() {

        try (ServerSocket server = new ServerSocket(serverConfig.getPort())) {

            ExecutorService executor = Executors.newFixedThreadPool(serverConfig.getThread());

            HttpRequestProcessor httpRequestProcessor = new HttpRequestProcessor(serverConfig.getHosts());

            logger.info("Accepting connections on port " + server.getLocalPort());

            while (true) {

                try {

                    Socket connection = server.accept();

                    Runnable connectionHandler = new ConnectionHandler(connection, httpRequestProcessor);

                    executor.submit(connectionHandler);

                } catch (IOException ex) {
                    logger.warn("Error accepting connection", ex);
                }
            }

        } catch (IOException ex) {
            logger.error("Server could not start", ex);
        }
    }

    public static void main(String[] args) throws Exception {

        ServerConfig serverConfig;

        if (args != null && args.length > 0) {

            if (!"--config".equals(args[0])) {
                throw new IllegalArgumentException("illegal commend argument");
            }

            serverConfig = ServerConfigurer.createServerConfig(new File(args[1]));

        } else {
            serverConfig = ServerConfigurer.createDefaultServerConfig();
        }

        CycatServer server = new CycatServer(serverConfig);
        server.start();
    }

}