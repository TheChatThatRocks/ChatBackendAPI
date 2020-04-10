package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.SEND_MESSAGE_TO_USER)
public class SendMessageToRoomCommand extends BasicPackage {
    private String roomName;
    private byte[] file;

    @SuppressWarnings("unused")
    public SendMessageToRoomCommand() {
    }

    @SuppressWarnings("unused")
    public SendMessageToRoomCommand(int messageId, String roomName, byte[] file) {
        super(messageId);
        this.roomName = roomName;
        this.file = file;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
