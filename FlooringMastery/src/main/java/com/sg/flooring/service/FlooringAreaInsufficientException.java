package com.sg.flooring.service;

public class FlooringAreaInsufficientException extends Exception {

    public FlooringAreaInsufficientException(String msg) {
        super(msg);
    }

    public FlooringAreaInsufficientException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
