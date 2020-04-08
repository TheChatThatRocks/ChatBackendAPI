package com.eina.chat.backendapi.protocol.packages;

public class AddAccount extends BasicPackage {
    private String username;
    private String password;

    @SuppressWarnings("unused")
    public AddAccount() {
        super();
    }

    @SuppressWarnings("unused")
    public AddAccount(int messageId, String username, String password) {
        super(TypeOfMessage.ADD_ACCOUNT, messageId);
        this.username = username;
        this.password = password;
    }


    public AddAccount(TypeOfMessage typeOfMessage, int messageId, String username, String password) {
        super(typeOfMessage, messageId);
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
