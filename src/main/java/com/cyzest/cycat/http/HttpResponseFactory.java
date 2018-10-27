package com.cyzest.cycat.http;

import java.io.*;

public class HttpResponseFactory {

    public static HttpResponse createDefaultHttpResponse(OutputStream responseOutputStream) {

        OutputStream outputStream = new DataOutputStream(responseOutputStream);

        Writer writer = new OutputStreamWriter(outputStream);

        DefaultHttpResponse defaultHttpResponse = new DefaultHttpResponse();

        defaultHttpResponse.setWriter(writer);

        return defaultHttpResponse;
    }

    private static class DefaultHttpResponse implements HttpResponse {

        private HttpStatus httpStatus;

        private String contentType;

        private Writer writer;

        @Override
        public HttpStatus getHttpStatus() {
            return httpStatus;
        }

        @Override
        public void setHttpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

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
