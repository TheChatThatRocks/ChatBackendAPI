package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.DELETE_ROOM)
public class DeleteRoomCommand extends BasicPackage {
    private String roomName;

    @SuppressWarnings("unused")
    public DeleteRoomCommand() {
    }

    @SuppressWarnings("unused")
    public DeleteRoomCommand(int messageId, String roomName) {
        super(messageId);
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
