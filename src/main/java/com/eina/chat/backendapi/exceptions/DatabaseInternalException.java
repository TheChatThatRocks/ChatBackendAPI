package com.eina.chat.backendapi.exceptions;

/**
 * Exception caused by problem in the Database
 */
public class DatabaseInternalException extends Exception {
    public DatabaseInternalException(String message) {
        super(message);
    }
}