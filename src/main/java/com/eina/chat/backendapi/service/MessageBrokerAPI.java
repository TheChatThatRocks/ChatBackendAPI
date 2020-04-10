package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.rabbitmq.ConsumersContainer;
import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import com.eina.chat.backendapi.rabbitmq.Consumer;
import com.eina.chat.backendapi.rabbitmq.Producer;
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

    public void createUser(String username) {
        // Create a queue
        Queue myQueue = QueueBuilder.durable(username).build();
        rabbitAdmin.declareQueue(myQueue);
        Binding privateMsg = BindingBuilder.bind(myQueue).to(topic).with("*.*." + username);
        rabbitAdmin.declareBinding(privateMsg);
        logger.info("[" + username + "] User has been created");
    }

    public void deleteUser(String username){
        // Disconnect and delete user
        disconnectUser(username);
        rabbitAdmin.deleteQueue(username);
        logger.info("[" + username + "] User has been deleted");
    }

    public void addUserToGroup(String username){
        // TODO:
    }

    public void removeUserToGroup(String username) {
        // TODO:
    }

    public void connectUser(String username, ReceiveHandler userHandler) {
        //Create a listener/consumer
        SimpleMessageListenerContainer  listenerContainer = new Consumer(username, userHandler).listenerContainer(connectionFactory);

        //Start the consumer and add to the list
        consumersContainer.addConsumer(username, listenerContainer);
        listenerContainer.start();
        logger.info("[" + username + "] Connecting...");

    }

    public void disconnectUser(String username) {
        consumersContainer.getConsumer(username).stop();
        consumersContainer.deleteConsumer(username);
        logger.info("[" + username + "] Disconnecting...");
    }

    public void sendMessageToUser(String userFrom, String userTo, String message) {
        producer.send(userFrom + ".*." + userTo, message);
        logger.info("[" + userFrom + "] Sent to [" + userTo + "]: " + message);
    }
}
