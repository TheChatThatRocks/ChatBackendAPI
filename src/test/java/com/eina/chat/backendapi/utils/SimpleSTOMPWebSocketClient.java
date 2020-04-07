package com.eina.chat.backendapi.utils;

import org.springframework.web.socket.WebSocketHttpHeaders;

public class SimpleSTOMPWebSocketClient {
    public SimpleSTOMPWebSocketClient() {
    }

    boolean connect(String url, WebSocketHttpHeaders headers, int port) {
        return true;
    }

    void setOnErrorCallback(OnErrorCallback onErrorCallback){

    }

    <T> void subscribe(int destination, OnMessageCallback<T> onMessageCallback) {

    }

    void send(int destination, Object payload) {

    }

    void disconnect() {

    }

    public interface OnMessageCallback<T>{
        void onMessageArrive(T message);
    }

    public interface OnErrorCallback {
        void onErrorArrive(String description);
    }
}
