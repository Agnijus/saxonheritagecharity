package com.example.saxonheritage.services;
public class GlobalCustomException extends Exception {
    private int statusCode;
    private String message;

    public GlobalCustomException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
