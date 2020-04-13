package com.eina.chat.backendapi.protocol.packages.admin.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.SEND_MESSAGE_TO_ALL)
public class SendMessageToAllCommand extends BasicPackage {
    private String message;

    @SuppressWarnings("unused")
    public SendMessageToAllCommand() {
        super();
    }

    @SuppressWarnings("unused")
    public SendMessageToAllCommand(int messageId, String message) {
        super(messageId);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
