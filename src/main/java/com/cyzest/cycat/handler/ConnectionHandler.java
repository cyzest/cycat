package com.cyzest.cycat.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    private final Socket connection;

    private final HttpRequestProcessor httpRequestProcessor;

    public ConnectionHandler(Socket connection, HttpRequestProcessor httpRequestProcessor) {
        if (connection == null) {
            throw new IllegalArgumentException("construct argument connection not be null");
        }
        if (httpRequestProcessor == null) {
            throw new IllegalArgumentException("construct argument httpRequestProcessor not be null");
        }
        this.connection = connection;
        this.httpRequestProcessor = httpRequestProcessor;
    }

    @Override
    public void run() {

        logger.debug("Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            if (in.available() > 0) {
                httpRequestProcessor.process(in, out);
            }

        } catch (IOException ex) {
            logger.warn("connection io exception", ex);
        } finally {
            try {
                logger.debug("Connected Close IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
                connection.close();
            } catch (IOException ex) {
                logger.warn("connection close exception", ex);
            }
        }

    }

}
