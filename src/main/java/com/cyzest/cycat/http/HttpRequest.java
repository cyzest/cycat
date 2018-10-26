package com.cyzest.cycat.http;

import java.io.InputStream;
import java.util.Map;

public interface HttpRequest {

    String getHttpVersion();

    String getMethod();

    String getUrl();

    Map<String, String> getParameterMap();

    Map<String, String> getHeaderMap();

    String getHost();

    String getParameter(String name);

    String getHeader(String name);

    InputStream getBody();

}
