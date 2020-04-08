package com.eina.chat.backendapi.protocol.packages;

public class MessageFromUser extends BasicPackage {
    private String from;
    private String message;


    @SuppressWarnings("unused")
    public MessageFromUser() {
        super();
    }

    @SuppressWarnings("unused")
    public MessageFromUser(TypeOfMessage typeOfMessage, int messageId, String from, String message) {
        super(typeOfMessage, messageId);
        this.from = from;
        this.message = message;
    }

    public MessageFromUser(String from, String message) {
        super(TypeOfMessage.MESSAGE_FROM_USER, 0);
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
