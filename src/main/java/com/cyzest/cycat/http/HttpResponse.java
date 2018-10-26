package com.cyzest.cycat.http;

import java.io.Writer;

public interface HttpResponse {

    String getContentType();

    void setContentType(String contentType);

    Writer getWriter();

}
