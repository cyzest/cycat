package com.cyzest.cycat.service;

import com.cyzest.cycat.http.HttpRequest;
import com.cyzest.cycat.http.HttpResponse;
import com.cyzest.cycat.http.SimpleServlet;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CurrentTimeServlet implements SimpleServlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        Writer writer = httpResponse.getWriter();

        writer.write("현재시각 : " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        writer.flush();
    }

}
