package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;

@Service
public class MessageBrokerAPI {
    // TODO: Delete this, is only for testing while is not implemented
    // Callbacks
    HashMap<String, ReceiveHandler> callbacks = new HashMap<>();

    // TODO: Delete this, is only for testing while is not implemented
    // Groups
    HashMap<String, HashSet<String>> userGroups = new HashMap<>();


    /**
     * Notify broker than a new user have been created
     *
     * @param username user username
     */
    public void createUser(@NonNull String username) {
        // TODO:
    }

    /**
     * Notify broker than an user have been deleted
     *
     * @param username user username
     */
    public void deleteUser(@NonNull String username) {
        // TODO:
        for (HashSet<String> i : userGroups.values()) {
            i.remove(username);
        }
    }

    /**
     * Notify broker than an user have been added to a group
     *
     * @param username  user username
     * @param groupName group name
     */
    public void addUserToGroup(@NonNull String username, @NonNull String groupName) {
        // TODO:
        userGroups.get(groupName).add(username);
    }

    /**
     * Notify broker than an user have been removed from a group
     *
     * @param username  user username
     * @param groupName group name
     */
    public void removeUserFromGroup(@NonNull String username, @NonNull String groupName) {
        // TODO:
        userGroups.get(groupName).remove(username);
    }

    /**
     * Notify broker than an user have started session
     *
     * @param callable message receiver callback
     * @param username user username
     */
    public void addUserReceiverMessagesCallback(@NonNull String username, @NonNull ReceiveHandler callable) {
        // TODO:
        callbacks.put(username, callable);
    }

    /**
     * Notify broker than an user have end the session
     *
     * @param username user username
     */
    public void deleteUserReceiverMessagesCallback(@NonNull String username) {
        // TODO:
        System.out.println("Delete from callback -------------" + username);
        callbacks.remove(username);
    }


    /**
     * Notify broker to send message from user UserFrom to user UserTo
     *
     * @param usernameUserFrom UserFrom username
     * @param usernameUserTo   UserTo username
     * @param encryptedMessage encrypted message to send
     */
    public void sendMessageToUser(String usernameUserFrom, String usernameUserTo, String encryptedMessage) {
        // TODO:
        System.out.println("Message send en api ----------- antes lo contiene" + usernameUserTo);
        if (callbacks.containsKey(usernameUserTo)) {
            System.out.println("Message send en api ----------- lo contiene" + usernameUserTo);
            callbacks.get(usernameUserTo).onUserMessageArrive(usernameUserFrom, encryptedMessage);
        }
    }

    /**
     * Notify broker to send message from user UserFrom to group GroupTo
     *
     * @param usernameUserFrom UserFrom username
     * @param groupNameGroupTo GroupTo group name
     * @param encryptedMessage encrypted message to send
     */
    public void sendMessageToGroup(String usernameUserFrom, String groupNameGroupTo, String encryptedMessage) {
        // TODO:
        if (userGroups.containsKey(groupNameGroupTo)) {
            for (String usernameUserTo : userGroups.get(groupNameGroupTo)) {
                if (callbacks.containsKey(usernameUserTo))
                    callbacks.get(usernameUserTo).onGroupMessageArrive(usernameUserFrom, groupNameGroupTo, encryptedMessage);
            }
        }
    }

    /**
     * Notify broker to send file from user UserFrom to user UserTo
     *
     * @param usernameUserFrom UserFrom username
     * @param usernameUserTo   UserTo username
     * @param encryptedFile    encrypted file to send
     */
    public void sendFileToUser(String usernameUserFrom, String usernameUserTo, byte[] encryptedFile) {
        // TODO:
        if (callbacks.containsKey(usernameUserTo)) {
            callbacks.get(usernameUserTo).onUserFileArrive(usernameUserFrom, encryptedFile);
        }
    }

    /**
     * Notify broker to send file from user UserFrom to group GroupTo
     *
     * @param usernameUserFrom UserFrom username
     * @param groupNameGroupTo GroupTo group name
     * @param encryptedFile    encrypted file to send
     */
    public void sendFileToGroup(String usernameUserFrom, String groupNameGroupTo, byte[] encryptedFile) {
        // TODO:
        if (userGroups.containsKey(groupNameGroupTo)) {
            for (String usernameUserTo : userGroups.get(groupNameGroupTo)) {
                if (callbacks.containsKey(usernameUserTo))
                    callbacks.get(usernameUserTo).onGroupFileArrive(usernameUserFrom, groupNameGroupTo, encryptedFile);
            }
        }
    }
}
