package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.FILE_FROM_ROOM)
public class FileFromRoomResponse extends BasicPackage {
    private String fromUser;
    private String fromRoom;
    private byte[] file;

    @SuppressWarnings("unused")
    public FileFromRoomResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public FileFromRoomResponse(int messageId, String fromUser, String fromRoom, byte[] file) {
        super(messageId);
        this.fromUser = fromUser;
        this.fromRoom = fromRoom;
        this.file = file;
    }

    public FileFromRoomResponse(String fromUser, String fromRoom, byte[] file) {
        super(0);
        this.fromUser = fromUser;
        this.fromRoom = fromRoom;
        this.file = file;
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

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
