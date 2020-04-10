package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.MESSAGE_FROM_USER)
public class MessageFromUserResponse extends BasicPackage {
    private String from;
    private String message;


    @SuppressWarnings("unused")
    public MessageFromUserResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public MessageFromUserResponse(int messageId, String from, String message) {
        super(messageId);
        this.from = from;
        this.message = message;
    }

    public MessageFromUserResponse(String from, String message) {
        super(0);
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
