package com.cyzest.cycat.http;

import com.cyzest.cycat.http.exception.HttpFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestFactory.class);

    public static HttpRequest createDefaultHttpRequest(InputStream requestInputStream) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(requestInputStream, StandardCharsets.UTF_8));

        // Request Line Parsing

        String requestLine = reader.readLine();

        if (requestLine == null) {
            throw new HttpFormatException("Invalid HTTP Request");
        }

        logger.debug("requestLine : {}", requestLine);

        StringTokenizer requestToken = new StringTokenizer(requestLine);

        if (requestToken.countTokens() != 3) {
            throw new HttpFormatException("Invalid HTTP Request Line");
        }

        String method = requestToken.nextToken();
        String url = requestToken.nextToken();
        String httpVersion = requestToken.nextToken();

        // Header Line Parsing

        Map<String, String> headerMap = new HashMap<>();

        String headerLine = reader.readLine();

        while (headerLine.length() > 0) {

            int idx = headerLine.indexOf(":");

            if (idx == -1) {
                throw new HttpFormatException("Invalid HTTP Header Line");
            }

            headerMap.put(headerLine.substring(0, idx), headerLine.substring(idx + 2));

            headerLine = reader.readLine();
        }

        // Parameter Parsing

        Map<String, String> parameterMap = new HashMap<>();

        int idx = url.indexOf("?");

        if (idx != -1 && !url.matches("\\?&")) {

            String queryString = url.substring(idx + 1);

            String[] queries = queryString.split("&");

            for (String query : queries) {

                String[] param = query.split("=");

                if (param.length == 2) {
                    parameterMap.put(param[0], param[1]);
                }
            }

            url = url.substring(0, idx);
        }

        // URL 중복 슬래시 처리
        url = url.replaceAll("(/)+", "/");

        DefaultHttpRequest httpRequest = new DefaultHttpRequest();

        httpRequest.setMethod(method);
        httpRequest.setUrl(url);
        httpRequest.setHttpVersion(httpVersion);
        httpRequest.setParameterMap(parameterMap);
        httpRequest.setHeaderMap(headerMap);
        httpRequest.setBody(requestInputStream);

        return httpRequest;
    }

    private static class DefaultHttpRequest implements HttpRequest {

        private String httpVersion;
        private String method;
        private String url;
        private Map<String, String> parameterMap;
        private Map<String, String> headerMap;
        private InputStream body;

        @Override
        public String getHost() {
            String host = null;
            if (headerMap != null) {
                host = headerMap.get(HttpConstants.HOST_HEADER_NAME);
                if (host != null && !host.isEmpty()) {
                    host = host.split(":")[0];
                }
            }
            return host;
        }

        @Override
        public String getParameter(String name) {
            String parameter = null;
            if (parameterMap != null) {
                parameter = parameterMap.get(name);
            }
            return parameter;
        }

        @Override
        public String getHeader(String name) {
            String header = null;
            if (headerMap != null) {
                header = headerMap.get(name);
            }
            return header;
        }

        @Override
        public String getHttpVersion() {
            return httpVersion;
        }

        public void setHttpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
        }

        @Override
        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        @Override
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public Map<String, String> getParameterMap() {
            return parameterMap;
        }

        public void setParameterMap(Map<String, String> parameterMap) {
            this.parameterMap = parameterMap;
        }

        @Override
        public Map<String, String> getHeaderMap() {
            return headerMap;
        }

        public void setHeaderMap(Map<String, String> headerMap) {
            this.headerMap = headerMap;
        }

        @Override
        public InputStream getBody() {
            return body;
        }

        public void setBody(InputStream body) {
            this.body = body;
        }

    }

}
