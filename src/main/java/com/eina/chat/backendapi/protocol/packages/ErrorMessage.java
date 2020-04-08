package com.eina.chat.backendapi.protocol.packages;

public class ErrorMessage extends BasicPackage {
    private String description;

    @SuppressWarnings("unused")
    public ErrorMessage() {
        super();
    }

    @SuppressWarnings("unused")
    public ErrorMessage(TypeOfMessage typeOfMessage, int messageId, String description) {
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
