package com.cyzest.cycat.http;

import java.io.Writer;

public interface HttpResponse {

    HttpStatus getHttpStatus();

    void setHttpStatus(HttpStatus httpStatus);

    String getContentType();

    void setContentType(String contentType);

    Writer getWriter();

    void sendRedirect(String url);

    String getRedirectUrl();

}
