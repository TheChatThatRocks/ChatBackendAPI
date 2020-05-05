package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.GET_JOINED_ROOMS)
public class GetJoinedRoomsCommand extends BasicPackage {
    @SuppressWarnings("unused")
    public GetJoinedRoomsCommand() {
    }

    @SuppressWarnings("unused")
    public GetJoinedRoomsCommand(int messageId) {
        super(messageId);
    }
}
