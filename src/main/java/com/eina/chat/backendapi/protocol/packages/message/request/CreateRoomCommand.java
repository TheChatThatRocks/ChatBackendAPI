package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.CREATE_ROOM)
public class CreateRoomCommand extends BasicPackage {
    private String roomName;

    @SuppressWarnings("unused")
    public CreateRoomCommand() {
    }

    @SuppressWarnings("unused")
    public CreateRoomCommand(int messageId) {
        super(messageId);
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
