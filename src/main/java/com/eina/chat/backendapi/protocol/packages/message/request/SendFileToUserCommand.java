package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.SEND_MESSAGE_TO_USER)
public class SendFileToUserCommand extends BasicPackage {
    private String username;
    private String message;

    @SuppressWarnings("unused")
    public SendFileToUserCommand() {
    }

    @SuppressWarnings("unused")
    public SendFileToUserCommand(int messageId, String username, String message) {
        super(messageId);
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
