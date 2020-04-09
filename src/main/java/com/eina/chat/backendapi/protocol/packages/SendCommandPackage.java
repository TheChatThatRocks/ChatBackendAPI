package com.eina.chat.backendapi.protocol.packages;

public class SendCommandPackage {
    private TypeOfMessage typeOfMessage;
    private int messageId;
    private SendCommandArgument argument;

    @SuppressWarnings("unused")
    public SendCommandPackage() {
    }

    @SuppressWarnings("unused")
    public SendCommandPackage(TypeOfMessage typeOfMessage, int messageId, SendCommandArgument argument) {
        this.typeOfMessage = typeOfMessage;
        this.messageId = messageId;
        this.argument = argument;
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

    public SendCommandArgument getArgument() {
        return argument;
    }

    public void setArgument(SendCommandArgument argument) {
        this.argument = argument;
    }
}
