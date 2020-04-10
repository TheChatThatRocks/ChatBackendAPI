package com.eina.chat.backendapi.protocol.packages.signup.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.SIGN_UP_ERROR)
public class SignUpErrorResponse extends BasicPackage {
    private String description;

    @SuppressWarnings("unused")
    public SignUpErrorResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public SignUpErrorResponse(int messageId, String description) {
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
