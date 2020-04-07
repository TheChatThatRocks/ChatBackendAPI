package com.eina.chat.backendapi.protocol.packages;

public abstract class BasicPackage {
    private TypeOfMessage typeOfMessage;
    private int messageId;

    public BasicPackage() {
    }

    public BasicPackage(TypeOfMessage typeOfMessage, int messageId) {
        this.typeOfMessage = typeOfMessage;
        this.messageId = messageId;
    }

    public TypeOfMessage getTypeOfMessage() {
        return typeOfMessage;
    }

    public void setTypeOfMessage(TypeOfMessage typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

}
