package com.eina.chat.backendapi.exceptions;

public class LoginException extends Exception {
    private final String simpSessionId;

    public LoginException(String message, String simpSessionId) {
        super(message);
        this.simpSessionId = simpSessionId;
    }

    public String getSimpSessionId() {
        return simpSessionId;
    }
}
