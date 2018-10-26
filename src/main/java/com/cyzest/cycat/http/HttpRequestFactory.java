package com.cyzest.cycat.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequestFactory {

    public static HttpRequest createHttpRequest(InputStream requestInputStream) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(requestInputStream, StandardCharsets.UTF_8));

        // Request Line Parsing

        String requestLine = reader.readLine();

        StringTokenizer requestToken = new StringTokenizer(requestLine);

        if (requestToken.countTokens() != 3) {
            throw new IllegalAccessException("Invalid HTTP Request Line");
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
                throw new IllegalAccessException("Invalid HTTP Header Line");
            }

            headerMap.put(headerLine.substring(0, idx), headerLine.substring(idx + 2));

            headerLine = reader.readLine();
        }

        // Parameter Parsing

        Map<String, String> parameterMap = new HashMap<>();

        int idx = url.indexOf("?");

        if (idx != -1) {

            String queryString = url.substring(idx + 1);

            url = url.substring(0, idx);
        }

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
                host = headerMap.get("Host");
                if (host != null && !host.isEmpty()) {
                    host = host.split(":")[0];
                }
            }
            return host;
        }

        @Override
        public String getParameter(String name) {
            return null;
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
