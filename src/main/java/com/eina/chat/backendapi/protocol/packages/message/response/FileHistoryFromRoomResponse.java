package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName(TypesOfMessage.FILE_HISTORY_FROM_ROOM)
public class FileHistoryFromRoomResponse extends BasicPackage {
    private String fromRoom;
    private List<String> fromUsers;
    private List<byte[]> files;

    @SuppressWarnings("unused")
    public FileHistoryFromRoomResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public FileHistoryFromRoomResponse(int messageId, String fromRoom, List<String> fromUsers, List<byte[]> files) {
        super(messageId);
        this.fromRoom = fromRoom;
        this.fromUsers = fromUsers;
        this.files = files;
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

    public List<byte[]> getFiles() {
        return files;
    }

    public void setFiles(List<byte[]> files) {
        this.files = files;
    }
}
