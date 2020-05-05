package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName(TypesOfMessage.ADMINISTERED_ROOMS)
public class AdministeredRoomsResponse extends BasicPackage {
    private List<String> roomsName;

    @SuppressWarnings("unused")
    public AdministeredRoomsResponse() {
        super();
    }

    public AdministeredRoomsResponse(int messageId, List<String> roomsName) {
        super(messageId);
        this.roomsName = roomsName;
    }

    public List<String> getRoomsName() {
        return roomsName;
    }

    public void setRoomsName(List<String> roomsName) {
        this.roomsName = roomsName;
    }
}
