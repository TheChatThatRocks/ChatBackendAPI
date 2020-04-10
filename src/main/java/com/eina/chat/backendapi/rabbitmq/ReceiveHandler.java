package com.eina.chat.backendapi.rabbitmq;

public interface ReceiveHandler {
    void onUserMessageArrive(String user, String message);

    void onGroupMessageArrive(String user, String group, String message);
}