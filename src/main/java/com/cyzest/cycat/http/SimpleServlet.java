package com.cyzest.cycat.http;

import java.io.IOException;

public interface SimpleServlet {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;

}
