package com.cyzest.cycat.service;

import com.cyzest.cycat.http.HttpRequest;
import com.cyzest.cycat.http.HttpResponse;
import com.cyzest.cycat.http.SimpleServlet;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalTime;

public class CurrentTimeServlet implements SimpleServlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        Writer writer = httpResponse.getWriter();

        writer.write(LocalTime.now().toString());

        writer.flush();
    }
}
