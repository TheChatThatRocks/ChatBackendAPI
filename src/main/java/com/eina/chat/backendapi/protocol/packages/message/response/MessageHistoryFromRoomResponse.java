package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName(TypesOfMessage.MESSAGE_HISTORY_FROM_ROOM)
public class MessageHistoryFromRoomResponse extends BasicPackage {
    private String fromRoom;
    private List<String> fromUsers;
    private List<String> messages;

    @SuppressWarnings("unused")
    public MessageHistoryFromRoomResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public MessageHistoryFromRoomResponse(int messageId, String fromRoom, List<String> fromUsers, List<String> messages) {
        super(messageId);
        this.fromRoom = fromRoom;
        this.fromUsers = fromUsers;
        this.messages = messages;
    }

    public String getFromRoom() {
        return fromRoom;
    }

    public void setFromRoom(String fromRoom) {
        this.fromRoom = fromRoom;
    }

    public List<String> getFromUsers() {
        return fromUsers;
    }

    public void setFromUsers(List<String> fromUsers) {
        this.fromUsers = fromUsers;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
