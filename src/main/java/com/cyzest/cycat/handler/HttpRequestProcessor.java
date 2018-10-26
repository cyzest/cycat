package com.cyzest.cycat.handler;

import com.cyzest.cycat.config.HostInfo;
import com.cyzest.cycat.exception.HttpSatusException;
import com.cyzest.cycat.http.*;
import com.cyzest.cycat.security.DirectoryUrlPatternSecurity;
import com.cyzest.cycat.security.FileExtensionUrlPatternSecurity;
import com.cyzest.cycat.security.UrlPatternSecurityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestProcessor.class);

    private final Map<String, HostInfo> hostInfoMap = new HashMap<>();

    private final Map<String, UrlPatternSecurityChecker> hostUrlPatternSecurityCheckerMap = new HashMap<>();

    public HttpRequestProcessor(List<HostInfo> hostInfos) throws IOException {

        if (hostInfos == null) {
            throw new IllegalArgumentException("hostInfos is not be null");
        }

        for (HostInfo hostInfo : hostInfos) {

            this.hostInfoMap.put(hostInfo.getHost(), hostInfo);

            UrlPatternSecurityChecker urlPatternSecurityChecker = new UrlPatternSecurityChecker();

            urlPatternSecurityChecker.addUrlPatternSecurity(new FileExtensionUrlPatternSecurity());

            String documentRoot = hostInfo.getRoot();

            if (documentRoot != null && !documentRoot.isEmpty()) {
                urlPatternSecurityChecker.addUrlPatternSecurity(new DirectoryUrlPatternSecurity(documentRoot));
            }

            this.hostUrlPatternSecurityCheckerMap.put(hostInfo.getHost(), urlPatternSecurityChecker);
        }
    }

    public void process(InputStream inputStream, OutputStream outputStream) {

        try (ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream()) {

            HttpRequest httpRequest = HttpRequestFactory.createHttpRequest(inputStream);

            logger.debug("Method: {}", httpRequest.getMethod());
            logger.debug("URL: {}", httpRequest.getUrl());
            logger.debug("HttpVersion: {}", httpRequest.getHttpVersion());
            logger.debug("Host: {}", httpRequest.getHost());

            HttpResponse httpResponse = HttpResponseFactory.createHttpResponse(responseOutputStream);

            // 호스트 별 분기 처리

            String host = httpRequest.getHost();

            HostInfo hostInfo = hostInfoMap.get(host);

            if (hostInfo == null) {
                hostInfo = hostInfoMap.get("localhost");
            }

            // URL 맵핑 서블릿 처리

            String url = httpRequest.getUrl();

            Map<String, String> servletMapping = hostInfo.getServletMapping();

            if (servletMapping != null && servletMapping.containsKey(url)) {

                String servletClassName = servletMapping.get(url);

                SimpleServlet servlet = (SimpleServlet) Class.forName(servletClassName).newInstance();

                httpResponse.setContentType("application/text; charset=utf-8");

                servlet.service(httpRequest, httpResponse);

            } else {

                // 서블릿이 없으면 URL 맵핑 페이지 처리

                String docRoot = hostInfo.getRoot();

                if (docRoot != null && !docRoot.isEmpty()) {

                    File htmlFile;

                    if (url.matches("\\.(\\w)+&")) {
                        htmlFile = new File(docRoot + url);
                    } else {
                        String index = hostInfo.getIndex();
                        htmlFile = new File(docRoot + index);
                    }

                    if (!htmlFile.isFile()) {
                        throw new HttpSatusException(404);
                    }

                    // URL 보안 규칙 처리

                    if (!hostUrlPatternSecurityCheckerMap.get(host).validate(url)) {
                        throw new HttpSatusException(403);
                    }

                    httpResponse.setContentType(URLConnection.getFileNameMap().getContentTypeFor(url));

                    String body = new String(Files.readAllBytes(htmlFile.toPath()), StandardCharsets.UTF_8);

                    httpResponse.getWriter().write(body);
                    httpResponse.getWriter().flush();

                } else {

                    // Document Root 설정이 없으면 디폴트 인덱스 페이지 처리

                    if ("/".equals(url)) {

                        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("html/index.html")) {

                            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(in))) {
                                httpResponse.getWriter().write(buffer.lines().collect(Collectors.joining("\n")));
                                httpResponse.getWriter().flush();
                            }
                        }

                    } else {
                        throw new HttpSatusException(404);
                    }
                }
            }

            // 리스폰스 세팅

            Writer writer = new OutputStreamWriter(new BufferedOutputStream(outputStream));

            sendHeader(writer, httpRequest.getHttpVersion() + " 200 OK",
                    "application/text; charset=utf-8", responseOutputStream.size());

            writer.write(responseOutputStream.toString(StandardCharsets.UTF_8.name()));
            writer.flush();

        } catch (Throwable ex) {
            logger.error("Http Request Processing Error", ex);
        }
    }

    private void sendHeader(Writer out, String responseCode, String contentType, int length) throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: Cycat 2.0\r\n");
        out.write("Content-length: " + length + "\r\n");
        out.write("Content-type: " + contentType + "\r\n\r\n");
        out.flush();
    }

}