package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.GET_ADMINISTERED_ROOMS)
public class GetAdministeredRoomsCommand extends BasicPackage {
    @SuppressWarnings("unused")
    public GetAdministeredRoomsCommand() {
    }

    @SuppressWarnings("unused")
    public GetAdministeredRoomsCommand(int messageId) {
        super(messageId);
    }
}
