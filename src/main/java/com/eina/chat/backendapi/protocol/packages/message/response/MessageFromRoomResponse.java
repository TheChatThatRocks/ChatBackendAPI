package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.MESSAGE_FROM_ROOM)
public class MessageFromRoomResponse extends BasicPackage {
    private String fromUser;
    private String fromRoom;
    private String message;

    @SuppressWarnings("unused")
    public MessageFromRoomResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public MessageFromRoomResponse(int messageId, String fromUser, String fromRoom, String message) {
        super(messageId);
        this.fromUser = fromUser;
        this.fromRoom = fromRoom;
        this.message = message;
    }

    public MessageFromRoomResponse(String fromUser, String fromRoom, String message) {
        super(0);
        this.fromUser = fromUser;
        this.fromRoom = fromRoom;
        this.message = message;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getFromRoom() {
        return fromRoom;
    }

    public void setFromRoom(String fromRoom) {
        this.fromRoom = fromRoom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
