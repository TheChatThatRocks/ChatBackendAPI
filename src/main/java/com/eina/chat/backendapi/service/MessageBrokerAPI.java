package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class MessageBrokerAPI {
    // TODO: Quitar
    HashMap<String, ReceiveHandler> callbacks = new HashMap<>();

    public void sendMessageToUser(String userFrom, String userTo, String message) {
        if (callbacks.containsKey(userTo)) {
            callbacks.get(userTo).onUserMessageArrive(userFrom, message);
        }
    }

    public void createUser(String username) {
        // TODO:
    }

    public void addUserToGroup(String username){
        // TODO:
    }

    public void removeUserToGroup(String username){
        // TODO:
    }

    /**
     * @param callable
     * @return callback id
     */
    public void addUserReceiverMessagesCallback(ReceiveHandler callable, String username) {
        // TODO:
        callbacks.put(username, callable);
    }

    public void deleteUserReceiverMessagesCallback(String username) {
        //callbacks.remove(callbackId);
    }

    public void deleteUser(String username) {
    }
}
