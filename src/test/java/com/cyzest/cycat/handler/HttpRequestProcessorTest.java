package com.cyzest.cycat.handler;

import com.cyzest.cycat.config.HostInfo;
import com.cyzest.cycat.config.ServerConfig;
import com.cyzest.cycat.config.ServerConfigurer;
import com.cyzest.cycat.http.HttpStatus;
import com.cyzest.cycat.http.exception.HttpFormatException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

import static com.cyzest.cycat.http.HttpConstants.*;

public class HttpRequestProcessorTest {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestProcessorTest.class);

    private HttpRequestProcessor defaultHttpRequestProcessor;

    @BeforeEach
    public void init() throws IOException {
        ServerConfig serverConfig = ServerConfigurer.createDefaultServerConfig();
        defaultHttpRequestProcessor = new HttpRequestProcessor(serverConfig.getHosts());
    }

    @Test
    public void 기본요청을_정상적으로_수행하는가_1() throws Exception {

        String rootRequestMessage = createRequestMessage(GET, "/", "localhost:8080");

        ResponseMessage rootResponseMessage = processRequest(rootRequestMessage, defaultHttpRequestProcessor);

        Assertions.assertNotNull(rootResponseMessage);
        Assertions.assertEquals("HTTP/1.1", rootResponseMessage.getVersion());
        Assertions.assertEquals(HttpStatus.OK.getCode(), rootResponseMessage.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getMessage(), rootResponseMessage.getStatusMessage());
        Assertions.assertNotNull(rootResponseMessage.getBody());
    }

    @Test
    public void 기본요청을_정상적으로_수행하는가_2() throws Exception {

        String indexRequestMessage = createRequestMessage(GET, "/index.html", "localhost:8080");

        ResponseMessage indexResponseMessage = processRequest(indexRequestMessage, defaultHttpRequestProcessor);

        Assertions.assertNotNull(indexResponseMessage);
        Assertions.assertEquals("HTTP/1.1", indexResponseMessage.getVersion());
        Assertions.assertEquals(HttpStatus.OK.getCode(), indexResponseMessage.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getMessage(), indexResponseMessage.getStatusMessage());
        Assertions.assertNotNull(indexResponseMessage.getBody());
    }

    @Test
    public void 호스트_헤더별로_다른결과를_수행하는가() throws Exception {

        HostInfo aDotCom = createServletTestHostInfo("a.com", "com.cyzest.cycat.service.CurrentTimeServlet");
        HostInfo bDotCom = createServletTestHostInfo("b.com", "com.cyzest.cycat.handler.HelloWorldServlet");

        HttpRequestProcessor httpRequestProcessor = new HttpRequestProcessor(Arrays.asList(aDotCom, bDotCom));

        ResponseMessage aDotComMessage =
                processRequest(createRequestMessage(GET, "/", "a.com:8080"), httpRequestProcessor);

        ResponseMessage bDotComMessage =
                processRequest(createRequestMessage(GET, "/", "b.com:8080"), httpRequestProcessor);

        Assertions.assertNotNull(aDotComMessage);
        Assertions.assertNotNull(aDotComMessage.getBody());

        Assertions.assertNotNull(bDotComMessage);
        Assertions.assertNotNull(bDotComMessage.getBody());

        Assertions.assertNotEquals(aDotComMessage.getBody(), bDotComMessage.getBody());
    }

    @Test
    public void 없는_리소스_호출에_대한_오류결과를_반환하는가() throws Exception {

        String requestMessage = createRequestMessage(GET, "/test", "localhost:8080");

        ResponseMessage responseMessage = processRequest(requestMessage, defaultHttpRequestProcessor);

        Assertions.assertNotNull(responseMessage);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.getCode(), responseMessage.getStatusCode());
        Assertions.assertNotNull(responseMessage.getBody());
    }

    @Test
    public void 상위디렉토리_호출에_대한_오류결과를_반환하는가() throws Exception {

        String requestMessage = createRequestMessage(GET, "/../conf/config.json", "localhost:8080");

        ResponseMessage responseMessage = processRequest(requestMessage, defaultHttpRequestProcessor);

        Assertions.assertNotNull(responseMessage);
        Assertions.assertEquals(HttpStatus.FORBIDDEN.getCode(), responseMessage.getStatusCode());
        Assertions.assertNotNull(responseMessage.getBody());
    }

    @Test
    public void 실행파일_호출에_대한_오류결과를_반환하는가() throws Exception {

        String requestMessage = createRequestMessage(GET, "/test.exe", "localhost:8080");

        ResponseMessage responseMessage = processRequest(requestMessage, defaultHttpRequestProcessor);

        Assertions.assertNotNull(responseMessage);
        Assertions.assertEquals(HttpStatus.FORBIDDEN.getCode(), responseMessage.getStatusCode());
        Assertions.assertNotNull(responseMessage.getBody());
    }

    @Test
    public void 에러_호출에_대한_오류결과를_반환하는가() throws Exception {

        HostInfo hostInfo = createServletTestHostInfo(HostInfo.DEFAULT_HOST, "com.cyzest.cycat.handler.ErrorTestServlet");

        HttpRequestProcessor httpRequestProcessor = new HttpRequestProcessor(Collections.singletonList(hostInfo));

        String requestMessage = createRequestMessage(GET, "/", "localhost:8080");

        ResponseMessage responseMessage = processRequest(requestMessage, httpRequestProcessor);

        Assertions.assertNotNull(responseMessage);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), responseMessage.getStatusCode());
        Assertions.assertNotNull(responseMessage.getBody());
    }

    private ResponseMessage processRequest(
            String requestMessage, HttpRequestProcessor httpRequestProcessor) throws Exception {
        try(InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            httpRequestProcessor.process(inputStream, outputStream);
            return convertResponseMessage(new String(outputStream.toByteArray()));
        }
    }

    private String createRequestMessage(String method, String url, String host) {
        StringBuilder builder = new StringBuilder();
        builder.append(method).append(" ").append(url).append(" ").append("HTTP/1.1").append(CRLF);
        builder.append(HOST_HEADER_NAME).append(": ").append(host).append(CRLF);
        builder.append(CRLF);
        String requestMessage = builder.toString();
        logger.debug(requestMessage);
        return requestMessage;
    }

    private ResponseMessage convertResponseMessage(String message) throws Exception {

        logger.debug(message);

        try (StringReader stringReader = new StringReader(message);
             Scanner scanner = new Scanner(stringReader)) {

            ResponseMessage responseMessage = new ResponseMessage();

            String startLine = scanner.nextLine();

            String[] starLineToken = startLine.split(" ", 3);

            responseMessage.setVersion(starLineToken[0]);
            responseMessage.setStatusCode(Integer.parseInt(starLineToken[1]));
            responseMessage.setStatusMessage(starLineToken[2]);

            String headerLine = scanner.nextLine();
            while (headerLine.length() > 0) {
                int idx = headerLine.indexOf(":");
                if (idx == -1) {
                    throw new HttpFormatException("Invalid HTTP Header Line");
                }
                String headerName = headerLine.substring(0, idx);
                String headerValue = headerLine.substring(idx + 2);
                if (CONTENT_TYPE_HEADER_NAME.equals(headerName)) {
                    responseMessage.setContentType(headerValue);
                } else if (CONTENT_LENGTH_HEADER_NAME.equals(headerName)) {
                    responseMessage.setContentLength(headerValue);
                }
                headerLine = scanner.nextLine();
            }

            StringBuilder bodyLine = new StringBuilder();
            while (scanner.hasNext()) {
                bodyLine.append(scanner.nextLine());
            }
            responseMessage.setBody(bodyLine.toString());

            return responseMessage;
        }
    }

    private HostInfo createServletTestHostInfo(String host, String servletClassName) {
        HostInfo hostInfo = new HostInfo();
        hostInfo.setHost(host);
        hostInfo.setRoot("html");
        hostInfo.setIndex("index.html");
        Map<String, String> servletMapping = new HashMap<>();
        servletMapping.put("/", servletClassName);
        hostInfo.setServletMapping(servletMapping);
        return hostInfo;
    }

    private class ResponseMessage {

        private String version;
        private int statusCode;
        private String statusMessage;
        private String contentType;
        private String contentLength;
        private String body;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentLength() {
            return contentLength;
        }

        public void setContentLength(String contentLength) {
            this.contentLength = contentLength;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

}
