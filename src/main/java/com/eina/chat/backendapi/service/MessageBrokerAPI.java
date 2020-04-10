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

    public interface ReceiveHandler {
        void onUserMessageArrive(String user, String message);
        void onGroupMessageArrive(String user, String group, String message);
    }

    public boolean createUser(String username, ReceiveHandler userHandler) {
        // Todo comprobar que no existe en disco, que hacer con los consumers de los que se desconectan?
        // Create a queue
        Queue myQueue = QueueBuilder.durable(username).build();
        rabbitAdmin.declareQueue(myQueue);
        Binding privateMsg = BindingBuilder.bind(myQueue).to(topic).with("*.*." + username);
        rabbitAdmin.declareBinding(privateMsg);
        try {
            //Create a listener/consumer
            SimpleMessageListenerContainer  listenerContainer = new Consumer(username, userHandler).listenerContainer(connectionFactory, myQueue, userHandler);

            //Start the consumer and add to the list
            if (consumersContainer.addConsumer(username, listenerContainer)){
                logger.info("[" + username + "] User has been created");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("[" + username + "] User already exists");
        return false;
    }

    public void sendMessageToUser(String userFrom, String userTo, String message) {
        // Todo: buscar que existe el usuario?, comprobar que está conectado
        producer.send(userFrom + ".*." + userTo, message);
        logger.info("[" + userFrom + "] Sent to [" + userTo + "]: " + message);
    }

    public boolean disconnectUser(String username) {
        SimpleMessageListenerContainer listener = consumersContainer.getConsumer(username);
        if (listener != null){
            listener.stop();
            logger.info("[" + username + "] Disconnecting...");
            return true;
        }
        logger.error("[" + username + "] User not exists");
        return false;
    }

    public boolean connectUser(String username) {
        SimpleMessageListenerContainer listener = consumersContainer.getConsumer(username);
        if (listener != null){
            if(!listener.isRunning())
                listener.start();
            logger.info("[" + username + "] Connecting...");
            return true;
        }
        logger.error("[" + username + "] User not exists");
        return false;
    }

    /* TODO: se van a cambiar los handlers??
    public void replaceReceiveHandler(String username, ReceiveHandler receiveHandler){
        SimpleMessageListenerContainer consumer = consumersContainer.getConsumer(username);
        if (consumer != null){
            if (consumer.isRunning()){
                consumer.stop();
                consumer. // creo que hay que guardar tb los Consumers, guardar en consumer el SMLC
                consumer.start();
                consumersContainer.addConsumer(username, consumer);

            }
        }
    }
    */

    public boolean deleteUser(String username){
        // Create a queue
        SimpleMessageListenerContainer listener = consumersContainer.getConsumer(username);
        if (listener != null){ // Delete user
            if (listener.isRunning()){
                listener.stop();
            }
            consumersContainer.deleteConsumer(username);
            logger.info("[" + username + "] User has been deleted");
            return true;
        }
        logger.error("[" + username + "] User not found");
        return false;
    }
}
