package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.SEND_MESSAGE_TO_ROOM)
public class SendMessageToRoomCommand extends BasicPackage {
    private String roomName;
    private String message;

    @SuppressWarnings("unused")
    public SendMessageToRoomCommand() {
    }

    @SuppressWarnings("unused")
    public SendMessageToRoomCommand(int messageId, String roomName, String message) {
        super(messageId);
        this.roomName = roomName;
        this.message = message;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSize(){
        return message.length();
    }
}
