package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.DELETE_USER_FROM_CHAT_ROOM)
public class DeleteUserFromChatRoom extends BasicPackage {
    private String roomName;
    private String username;

    @SuppressWarnings("unused")
    public DeleteUserFromChatRoom() {
    }

    @SuppressWarnings("unused")
    public DeleteUserFromChatRoom(int messageId, String roomName, String username) {
        super(messageId);
        this.roomName = roomName;
        this.username = username;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
