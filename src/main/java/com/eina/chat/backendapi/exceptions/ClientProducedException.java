package com.eina.chat.backendapi.exceptions;

public class ClientProducedException extends Exception {
    private final String simpSessionId;

    public ClientProducedException(String message, String simpSessionId) {
        super(message);
        this.simpSessionId = simpSessionId;
    }

    public String getSimpSessionId() {
        return simpSessionId;
    }
}
