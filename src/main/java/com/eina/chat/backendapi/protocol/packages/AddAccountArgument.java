package com.eina.chat.backendapi.protocol.packages;

public class AddAccountArgument extends SendCommandArgument {
    private String username;
    private String password;

    @SuppressWarnings("unused")
    public AddAccountArgument() {
    }

    @SuppressWarnings("unused")
    public AddAccountArgument(String username, String password) {
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
