package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.AUTH_LEVEL)
public class AuthLevelResponse extends BasicPackage {
    private String authLevel;

    @SuppressWarnings("unused")
    public AuthLevelResponse() {
        super();
    }

    public AuthLevelResponse(int messageId, String authLevel) {
        super(messageId);
        this.authLevel = authLevel;
    }

    public String getAuthLevel() {
        return authLevel;
    }

    public void setAuthLevel(String authLevel) {
        this.authLevel = authLevel;
    }
}
