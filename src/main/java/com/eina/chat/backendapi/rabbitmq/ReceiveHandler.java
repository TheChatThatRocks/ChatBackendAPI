package com.eina.chat.backendapi.rabbitmq;

public interface ReceiveHandler {
    /**
     * Callback for on message arrive from user
     *
     * @param username user username of the sender
     * @param message  message sent
     */
    void onUserMessageArrive(String username, String message);

    /**
     * Callback for on message arrive from user
     *
     * @param username sender username
     * @param group    group name
     * @param message  message sent
     */
    void onGroupMessageArrive(String username, String group, String message);

    /**
     * Callback for on message arrive from user
     *
     * @param username user username of the sender
     * @param file     file sent
     */
    void onUserFileArrive(String username, byte[] file);

    /**
     * Callback for on message arrive from user
     *
     * @param username sender username
     * @param group    group name
     * @param file     file sent
     */
    void onGroupFileArrive(String username, String group, byte[] file);
}