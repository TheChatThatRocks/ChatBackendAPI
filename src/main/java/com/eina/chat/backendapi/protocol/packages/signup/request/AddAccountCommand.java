package com.eina.chat.backendapi.protocol.packages.signup.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.ADD_ACCOUNT)
public class AddAccountCommand extends BasicPackage {
    private String username;
    private String password;

    @SuppressWarnings("unused")
    public AddAccountCommand() {
        super();
    }

    @SuppressWarnings("unused")
    public AddAccountCommand(int messageId, String username, String password) {
        super(messageId);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
