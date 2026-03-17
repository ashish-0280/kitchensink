package com.example.kitchensink.exception;

public class AdminDeleteNotAllowedException extends RuntimeException {
    public AdminDeleteNotAllowedException(String message) {
        super(message);
    }
}
