package com.cyzest.cycat.http;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class HttpResponseFactory {

    public static HttpResponse createHttpResponse(OutputStream responseOutputStream) throws Exception {

        OutputStream raw = new BufferedOutputStream(responseOutputStream);

        Writer writer = new OutputStreamWriter(raw);

        DefaultHttpResponse defaultHttpResponse = new DefaultHttpResponse();

        defaultHttpResponse.setWriter(writer);

        return defaultHttpResponse;
    }

    private static class DefaultHttpResponse implements HttpResponse {

        private String contentType;

        private Writer writer;

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        @Override
        public Writer getWriter() {
            return writer;
        }

        public void setWriter(Writer writer) {
            this.writer = writer;
        }
    }

}
