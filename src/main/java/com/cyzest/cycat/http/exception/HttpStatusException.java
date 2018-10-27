package com.cyzest.cycat.http.exception;

import com.cyzest.cycat.http.HttpStatus;

public class HttpStatusException extends Exception {

    private HttpStatus status;

    public HttpStatusException(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
