package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.GET_AUTH_LEVEL)
public class GetAuthLevelCommand extends BasicPackage {
    @SuppressWarnings("unused")
    public GetAuthLevelCommand() {
    }

    @SuppressWarnings("unused")
    public GetAuthLevelCommand(int messageId) {
        super(messageId);
    }
}
