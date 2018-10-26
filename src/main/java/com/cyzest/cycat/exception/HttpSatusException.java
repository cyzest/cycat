package com.cyzest.cycat.exception;

public class HttpSatusException extends Exception {

    private int status;

    public HttpSatusException(int status) {
        this.status = status;
    }

}
