package com.cyzest.cycat.http;

public class HttpStatusException extends Exception {

    private HttpStatus status;

    public HttpStatusException(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
