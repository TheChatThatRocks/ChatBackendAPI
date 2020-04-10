package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.DELETE_ACCOUNT)
public class DeleteAccountCommand extends BasicPackage {
    private String username;

    @SuppressWarnings("unused")
    public DeleteAccountCommand() {
    }

    @SuppressWarnings("unused")
    public DeleteAccountCommand(int messageId, String username) {
        super(messageId);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
