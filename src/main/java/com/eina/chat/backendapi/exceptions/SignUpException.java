package com.eina.chat.backendapi.exceptions;

public class SignUpException extends Exception {
    private final String simpSessionId;

    public SignUpException(String message, String simpSessionId) {
        super(message);
        this.simpSessionId = simpSessionId;
    }

    public String getSimpSessionId() {
        return simpSessionId;
    }
}
