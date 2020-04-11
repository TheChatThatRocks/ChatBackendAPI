package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.rabbitmq.ConsumersContainer;
import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import com.eina.chat.backendapi.rabbitmq.Consumer;
import com.eina.chat.backendapi.rabbitmq.Producer;
import lombok.NonNull;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageBrokerAPI {
    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private ConsumersContainer consumersContainer;

    @Autowired
    private Producer producer;

    @Autowired
    private TopicExchange topic;

    @Autowired
    Logger logger;

    /**
     * Notify broker than a new user have been created
     *
     * @param username user username
     */
    public void createUser(@NonNull String username) {
        // Create a queue
        Queue myQueue = QueueBuilder.durable(username).build();
        rabbitAdmin.declareQueue(myQueue);
        Binding privateMsg = BindingBuilder.bind(myQueue).to(topic).with("*.*." + username);
        rabbitAdmin.declareBinding(privateMsg);
        logger.info("[" + username + "] User has been created");
    }

    /**
     * Notify broker than an user have been deleted
     *
     * @param username user username
     */
    public void deleteUser(@NonNull String username) {
        // Disconnect and delete user
        deleteUserReceiverMessagesCallback(username);
        rabbitAdmin.deleteQueue(username);
        logger.info("[" + username + "] User has been deleted");
    }

    /**
     * Notify broker than a group have been deleted
     *
     * @param groupName group name
     */
    public void deleteGroup(@NonNull String groupName) {
        // TODO:
    }

    /**
     * Notify broker than an user have been added to a group
     *
     * @param username  user username
     * @param groupName group name
     */
    public void addUserToGroup(@NonNull String username, @NonNull String groupName) {
        // TODO:
    }

    /**
     * Notify broker than an user have been removed from a group
     *
     * @param username  user username
     * @param groupName group name
     */
    public void removeUserFromGroup(@NonNull String username, @NonNull String groupName) {
        // TODO:
    }

    /**
     * Notify broker than an user have started session
     *
     * @param callable message receiver callback
     * @param username user username
     */
    public void addUserReceiverMessagesCallback(@NonNull String username, @NonNull ReceiveHandler callable) {
        //Create a listener/consumer
        SimpleMessageListenerContainer  listenerContainer = new Consumer(username, callable).listenerContainer(connectionFactory);

        //Start the consumer and add to the list
        consumersContainer.addConsumer(username, listenerContainer);
        listenerContainer.start();
        logger.info("[" + username + "] Connecting...");

    }

    /**
     * Notify broker than an user have end the session
     *
     * @param username user username
     */
    public void deleteUserReceiverMessagesCallback(@NonNull String username) {
        consumersContainer.getConsumer(username).stop();
        consumersContainer.deleteConsumer(username);
        logger.info("[" + username + "] Disconnecting...");
    }


    /**
     * Notify broker to send message from user UserFrom to user UserTo
     *
     * @param usernameUserFrom UserFrom username
     * @param usernameUserTo   UserTo username
     * @param encryptedMessage encrypted message to send
     */
    public void sendMessageToUser(String usernameUserFrom, String usernameUserTo, String encryptedMessage) {
        producer.send(usernameUserFrom + ".*." + usernameUserTo, encryptedMessage);
        logger.info("[" + usernameUserFrom + "] Sent to [" + usernameUserTo + "]: " + encryptedMessage);
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
    }
}
