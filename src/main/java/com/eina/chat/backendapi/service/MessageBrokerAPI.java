package com.eina.chat.backendapi.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MessageBrokerAPI {

    // TODO: Quitar
    ArrayList<BrokerMessagePackage> callbacks = new ArrayList<>();

    public boolean sendMessageToUser(String userFrom, String userTo, String message) {
        for (BrokerMessagePackage i : callbacks){
            i.onUserMessageArrive(userFrom, message);
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
        callbacks.add(callable);
        return 0;
    }

    public void deleteUserReceiverMessagesCallback(int callbackId) {

    }

    public boolean deleteUser(String username){
        return true;
    }
}
