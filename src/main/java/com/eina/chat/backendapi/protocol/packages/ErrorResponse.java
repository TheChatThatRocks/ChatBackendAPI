package com.eina.chat.backendapi.protocol.packages;

public class ErrorResponse extends BasicPackage {
    private String description;

    @SuppressWarnings("unused")
    public ErrorResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public ErrorResponse(TypeOfMessage typeOfMessage, int messageId, String description) {
        super(typeOfMessage, messageId);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
