package com.eina.chat.backendapi.protocol.packages;

public class SendMessageToUser extends BasicPackage {
    private String username;
    private String message;

    @SuppressWarnings("unused")
    public SendMessageToUser() {
        super();
    }

    @SuppressWarnings("unused")
    public SendMessageToUser(TypeOfMessage typeOfMessage, int messageId, String username, String message) {
        super(typeOfMessage, messageId);
        this.username = username;
        this.message = message;
    }

    public SendMessageToUser(int messageId, String username, String message) {
        super(TypeOfMessage.SEND_MESSAGE_TO_USER, messageId);
        this.username = username;
        this.message = message;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
