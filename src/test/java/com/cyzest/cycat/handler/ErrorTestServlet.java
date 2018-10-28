package com.cyzest.cycat.handler;

import com.cyzest.cycat.http.HttpRequest;
import com.cyzest.cycat.http.HttpResponse;
import com.cyzest.cycat.http.SimpleServlet;

import java.io.IOException;

public class ErrorTestServlet implements SimpleServlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        throw new NullPointerException("error test servlet exception");

    }

}
