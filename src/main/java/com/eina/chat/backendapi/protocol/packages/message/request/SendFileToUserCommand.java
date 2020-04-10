package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.SEND_MESSAGE_TO_USER)
public class SendFileToUserCommand extends BasicPackage {
    private String username;
    private byte[] file;

    @SuppressWarnings("unused")
    public SendFileToUserCommand() {
    }

    @SuppressWarnings("unused")
    public SendFileToUserCommand(int messageId, String username, byte[] file) {
        super(messageId);
        this.username = username;
        this.file = file;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
