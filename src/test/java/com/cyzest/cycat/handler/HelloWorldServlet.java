package com.cyzest.cycat.handler;

import com.cyzest.cycat.http.HttpRequest;
import com.cyzest.cycat.http.HttpResponse;
import com.cyzest.cycat.http.SimpleServlet;

import java.io.IOException;
import java.io.Writer;

public class HelloWorldServlet implements SimpleServlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        Writer writer = httpResponse.getWriter();

        writer.write("Hello World!");

        writer.flush();
    }

}
