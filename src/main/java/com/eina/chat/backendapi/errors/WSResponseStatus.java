package com.eina.chat.backendapi.errors;

/**
 * Common status message for WebSockets endpoints
 */
public class WSResponseStatus {
    /**
     * Possible response status
     */
    public enum StatusType {
        SUCCESS, ERROR
    }

    /**
     * Status of the response
     */
    private StatusType statusType;

    /**
     * Status message
     */
    private String status;

    @SuppressWarnings("unused")
    public WSResponseStatus() {
    }

    public WSResponseStatus(StatusType statusType, String status) {
        this.status = status;
        this.statusType = statusType;
    }

    @SuppressWarnings("unused")
    public String getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public void setStatus(String status) {
        this.status = status;
    }

    @SuppressWarnings("unused")
    public StatusType getStatusType() {
        return statusType;
    }

    @SuppressWarnings("unused")
    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }
}
