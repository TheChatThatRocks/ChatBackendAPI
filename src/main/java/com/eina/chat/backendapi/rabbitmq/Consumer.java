package com.eina.chat.backendapi.rabbitmq;

import com.eina.chat.backendapi.service.MessageBrokerAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

public class Consumer {

    private static final Logger logger = LogManager.getLogger("rabbit");
    private final String username;
    private MessageBrokerAPI.ReceiveHandler apiReceiveHandler;
    public Consumer(String username, MessageBrokerAPI.ReceiveHandler apiHandlerMessage){
        this.username = username;
        this.apiReceiveHandler = apiHandlerMessage;
    }

    public SimpleMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory, Queue queue, MessageBrokerAPI.ReceiveHandler apiReceiveHandler) {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAutoStartup(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConcurrentConsumers(1);
        container.setExclusive(true);
        container.addQueues(queue);
        container.setMessageListener(new MessageListenerAdapter(new MessageHandler(), "receiveMessage"));
        return container;
    }

    private class MessageHandler{
        public void receiveMessage(String message){
            logger.info("[" + username + "] Received: " + message);
//            apiReceiveHandler.onUserMessageArrive(username, message);
        }
    }

    public void setApiReceiveHandler(MessageBrokerAPI.ReceiveHandler apiReceiveHandler) {
        this.apiReceiveHandler = apiReceiveHandler;
    }
}
