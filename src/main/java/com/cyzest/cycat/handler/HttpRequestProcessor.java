package com.cyzest.cycat.handler;

import com.cyzest.cycat.config.HostInfo;
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

public class HttpRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestProcessor.class);

    private static final String CRLF = "\r\n";

    private final Map<String, HostProcessInfo> hostProcessInfoMap = new HashMap<>();

    public HttpRequestProcessor(List<HostInfo> hostInfos) throws IOException {

        if (hostInfos == null) {
            throw new IllegalArgumentException("hostInfos is not be null");
        }

        for (HostInfo hostInfo : hostInfos) {

            HostProcessInfo hostProcessInfo = new HostProcessInfo(hostInfo);

            UrlPatternSecurityChecker urlPatternSecurityChecker = new UrlPatternSecurityChecker();

            urlPatternSecurityChecker.addUrlPatternSecurity(new FileExtensionUrlPatternSecurity());
            urlPatternSecurityChecker.addUrlPatternSecurity(new DirectoryUrlPatternSecurity(hostInfo.getRoot()));

            hostProcessInfo.setUrlPatternSecurityChecker(urlPatternSecurityChecker);

            this.hostProcessInfoMap.put(hostInfo.getHost(), hostProcessInfo);
        }
    }

    public void process(InputStream inputStream, OutputStream outputStream) {

        try (ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream()) {

            // HTTP 리퀘스트 & 리스폰스 정보 생성

            HttpRequest httpRequest = HttpRequestFactory.createHttpRequest(inputStream);

            logger.debug("Method: {}", httpRequest.getMethod());
            logger.debug("URL: {}", httpRequest.getUrl());
            logger.debug("HttpVersion: {}", httpRequest.getHttpVersion());

            if (logger.isDebugEnabled()) {
                httpRequest.getHeaderMap().forEach((key, value) -> logger.debug(key + ": " + value));
                httpRequest.getParameterMap().forEach((key, value) -> logger.debug(key + "=" + value));
            }

            HttpResponse httpResponse = HttpResponseFactory.createHttpResponse(responseOutputStream);

            // HTTP 리퀘스트 요청 처리

            processRequest(httpRequest, httpResponse, outputStream, responseOutputStream);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void processRequest(
            HttpRequest httpRequest, HttpResponse httpResponse,
            OutputStream outputStream, ByteArrayOutputStream responseOutputStream) {

        // 호스트 별 처리 정보 조회

        String host = httpRequest.getHost();

        HostProcessInfo hostProcessInfo = hostProcessInfoMap.get(host);

        if (hostProcessInfo == null) {
            hostProcessInfo = hostProcessInfoMap.get("localhost");
        }

        try {

            // 서블릿 맵핑 처리

            String url = httpRequest.getUrl();

            Map<String, String> servletMapping = hostProcessInfo.getServletMapping();

            if (servletMapping != null && servletMapping.containsKey(url)) {

                String servletClassName = servletMapping.get(url);

                SimpleServlet servlet = (SimpleServlet) Class.forName(servletClassName).newInstance();

                httpResponse.setHttpStatus(HttpStatus.OK);
                httpResponse.setContentType(ContentType.TEXT_HTML);

                servlet.service(httpRequest, httpResponse);

                String body = new String(responseOutputStream.toByteArray(), StandardCharsets.UTF_8);

                sendResponse(
                        outputStream, httpRequest.getHttpVersion(),
                        httpResponse.getHttpStatus(), httpResponse.getContentType(), body);

            } else {

                // 서블릿 맵핑이 없으면 URL 맵핑 페이지 처리

                processUrlPageResponse(outputStream, httpRequest, httpResponse, hostProcessInfo);
            }

        } catch (Exception ex) {

            // 오류 페이지 처리

            HttpStatus httpStatus;

            if (ex instanceof HttpStatusException) {
                HttpStatusException httpStatusException = (HttpStatusException) ex;
                httpStatus = httpStatusException.getStatus();
            } else {
                logger.error("Internal Server Error", ex);
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }

            processErrorPageResponse(httpStatus, outputStream, httpRequest, httpResponse, hostProcessInfo);
        }
    }

    private void processUrlPageResponse(
            OutputStream outputStream,
            HttpRequest httpRequest, HttpResponse httpResponse, HostProcessInfo hostProcessInfo) throws Exception {

        String body;

        File htmlFile;

        String url = httpRequest.getUrl();

        String documentRoot = hostProcessInfo.getRoot();

        String fileExtensionRegx = "^(.)+\\.(\\w)+$";

        if (url.matches(fileExtensionRegx)) {
            htmlFile = new File(documentRoot + url);
        } else {
            String index = hostProcessInfo.getIndex();
            htmlFile = new File(documentRoot + "/" + url + "/" + index);
        }

        if (!htmlFile.isFile()) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND);
        }

        if (!hostProcessInfo.getUrlPatternSecurityChecker().validate(url)) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN);
        }

        httpResponse.setContentType(URLConnection.getFileNameMap().getContentTypeFor(htmlFile.getName()));

        body = new String(Files.readAllBytes(htmlFile.toPath()), StandardCharsets.UTF_8);

        httpResponse.setHttpStatus(HttpStatus.OK);

        sendResponse(
                outputStream, httpRequest.getHttpVersion(),
                httpResponse.getHttpStatus(), httpResponse.getContentType(), body);

    }

    private void processErrorPageResponse(
            HttpStatus httpStatus, OutputStream outputStream,
            HttpRequest httpRequest, HttpResponse httpResponse, HostProcessInfo hostProcessInfo) {

        String body = null;

        httpResponse.setHttpStatus(httpStatus);

        Map<String, String> errorPageMap = hostProcessInfo.getErrorPage();

        if (errorPageMap != null && !errorPageMap.isEmpty()) {

            String errorPage = errorPageMap.get(String.valueOf(httpStatus.getCode()));

            if (errorPage != null && !errorPage.isEmpty()) {

                File htmlFile = new File(hostProcessInfo.getRoot() + "/" + errorPage);

                if (htmlFile.isFile() && htmlFile.canRead()) {
                    try {
                        httpResponse.setContentType(URLConnection.getFileNameMap().getContentTypeFor(errorPage));
                        body = new String(Files.readAllBytes(htmlFile.toPath()), StandardCharsets.UTF_8);
                    } catch (Exception ex) {
                        logger.warn("htmlFile readAllBytes io exception", ex);
                    }
                }
            }
        }

        sendResponse(
                outputStream, httpRequest.getHttpVersion(),
                httpResponse.getHttpStatus(), httpResponse.getContentType(), body);

    }

    private void sendResponse(
            OutputStream outputStream,
            String httpVersion, HttpStatus httpStatus, String contentType, String body) {

        try {

            Writer writer = new OutputStreamWriter(new BufferedOutputStream(outputStream));

            writer.write(httpVersion + " " + httpStatus.getCode() + " " + httpStatus.getMessage() + CRLF);

            writer.write("Date: " + new Date() + CRLF);
            writer.write("Server: Cycat 2.0" + CRLF);

            if (contentType != null) {
                writer.write("Content-type: " + contentType + CRLF);
            }

            writer.write("Content-length: " + (body != null ? body.length() : 0) + CRLF + CRLF);

            if (body != null) {
                writer.write(body);
            }

            writer.flush();

        } catch (Exception ex) {
            logger.warn("writer io exception", ex);
        }
    }

}