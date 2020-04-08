package com.eina.chat.backendapi.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class MessageBrokerAPI {
    // TODO: Quitar
    HashMap<String, BrokerMessagePackage> callbacks = new HashMap<>();

    public boolean sendMessageToUser(String userFrom, String userTo, String message) {
        if (callbacks.containsKey(userTo)) {
            callbacks.get(userTo).onUserMessageArrive(userFrom, message);
        }
        return true;
    }

    public boolean createUser(String username) {
        // TODO:
        return true;
    }

    public interface BrokerMessagePackage {
        void onUserMessageArrive(String user, String message);

        void onGroupMessageArrive(String user, String group, String message);
    }

    /**
     * @param callable
     * @return callback id
     */
    public int addUserReceiverMessagesCallback(BrokerMessagePackage callable, String username) {
        // TODO:
        callbacks.put(username, callable);
        return 0;
    }

    public void deleteUserReceiverMessagesCallback(int callbackId) {
        //callbacks.remove(callbackId);
    }

    public boolean deleteUser(String username) {
        return true;
    }
}
