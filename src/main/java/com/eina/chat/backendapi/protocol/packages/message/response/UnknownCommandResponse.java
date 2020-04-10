package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.UNKNOWN_COMMAND)
public class UnknownCommandResponse extends BasicPackage {
    @SuppressWarnings("unused")
    public UnknownCommandResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public UnknownCommandResponse(int messageId) {
        super(messageId);
    }
}