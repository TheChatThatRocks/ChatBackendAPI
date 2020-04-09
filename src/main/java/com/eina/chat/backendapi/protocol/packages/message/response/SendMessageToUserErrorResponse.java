package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.SEND_MESSAGE_TO_USER_ERROR)
public class SendMessageToUserErrorResponse  extends BasicPackage {
    @SuppressWarnings("unused")
    public SendMessageToUserErrorResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public SendMessageToUserErrorResponse(int messageId) {
        super(messageId);
    }
}
