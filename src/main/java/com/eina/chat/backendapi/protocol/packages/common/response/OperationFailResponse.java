package com.eina.chat.backendapi.protocol.packages.common.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.OPERATION_FAIL)
public class OperationFailResponse extends BasicPackage {
    private String description;

    @SuppressWarnings("unused")
    public OperationFailResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public OperationFailResponse(int messageId, String description) {
        super(messageId);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
