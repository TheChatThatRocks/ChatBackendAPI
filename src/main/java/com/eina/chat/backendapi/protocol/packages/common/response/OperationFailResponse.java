package com.eina.chat.backendapi.protocol.packages.common.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.OPERATION_FAIL)
public class OperationFailResponse extends BasicPackage {
    private String typeOfFail;
    private String description;

    @SuppressWarnings("unused")
    public OperationFailResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public OperationFailResponse(int messageId, String typeOfFail) {
        super(messageId);
        this.typeOfFail = typeOfFail;
        this.description = "";
    }

    @SuppressWarnings("unused")
    public OperationFailResponse(int messageId, String typeOfFail, String description) {
        super(messageId);
        this.typeOfFail = typeOfFail;
        this.description = description;
    }

    public String getTypeOfFail() {
        return typeOfFail;
    }

    public void setTypeOfFail(String typeOfFail) {
        this.typeOfFail = typeOfFail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
