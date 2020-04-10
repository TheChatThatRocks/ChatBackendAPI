package com.eina.chat.backendapi.protocol.packages.signup.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.SIGN_UP_SUCCESS)
public class SignUpSuccessResponse extends BasicPackage {
    @SuppressWarnings("unused")
    public SignUpSuccessResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public SignUpSuccessResponse(int messageId) {
        super(messageId);
    }
}
