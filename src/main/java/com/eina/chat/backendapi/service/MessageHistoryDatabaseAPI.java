package com.eina.chat.backendapi.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageHistoryDatabaseAPI {
    /**
     * Save message send from user UserFrom to group GroupTo
     *
     * @param usernameUserFrom UserFrom username
     * @param groupNameGroupTo GroupTo group name
     * @param encryptedMessage encrypted message to send
     */
    public void saveMessageFromGroup(String usernameUserFrom, String groupNameGroupTo, String encryptedMessage) {
        // TODO:
    }

    /**
     * Save from user UserFrom to group GroupTo
     *
     * @param usernameUserFrom UserFrom username
     * @param groupNameGroupTo GroupTo group name
     * @param encryptedFile    encrypted file to send
     */
    public void saveFileToGroup(String usernameUserFrom, String groupNameGroupTo, byte[] encryptedFile) {
        // TODO:
    }

    /**
     * Get all messages saved from group in the same order they was inserted
     * @param groupName group name
     */
    public List<Pair<String,String>> getOrderedMessagesFromGroup(String groupName){
        // TODO:
        return new ArrayList<>();
    }

    /**
     * Get all files saved from group in the same order they was inserted
     * @param groupName group name
     */
    public List<Pair<String,byte[]>> getOrderedFilesFromGroup(String groupName){
        // TODO:
        return new ArrayList<>();
    }
}
