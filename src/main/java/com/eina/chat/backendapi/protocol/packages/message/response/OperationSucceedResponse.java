package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.OPERATION_SUCCEED)
public class OperationSucceedResponse extends BasicPackage {
    @SuppressWarnings("unused")
    public OperationSucceedResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public OperationSucceedResponse(int messageId) {
        super(messageId);
    }

}