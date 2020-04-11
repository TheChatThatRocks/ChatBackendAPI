package com.eina.chat.backendapi.rabbitmq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import java.util.List;

public class Consumer implements MessageListener{

    private static final Logger logger = LogManager.getLogger("rabbit");
//    private final String username;
    private final ReceiveHandler userReceiveHandler;

    public Consumer(ReceiveHandler apiHandlerMessage){
        super();
//        this.username = username;
        this.userReceiveHandler = apiHandlerMessage;
    }

    /**
     * Create a new container to listen the queue username
     * @param username name of the queue
     * @param connectionFactory
     * @return
     */
    public SimpleMessageListenerContainer createListenerContainer(String username, ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAutoStartup(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setExclusive(true);
        container.addQueueNames(username);
        container.setMessageListener(new MessageListenerAdapter(this, "onMessage"));
        return container;
    }

    /**
     * Handler which receive a message and invoke the user callback
     * @param message message received
     */
    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody());
        MessageProperties msgProperties = message.getMessageProperties();
        String[] agents = msgProperties.getReceivedRoutingKey().split("\\.", 3);
        if (agents.length == 3){
            if (agents[1].equals("any")){// It's a private msg
                userReceiveHandler.onUserMessageArrive(agents[2], body);
                logger.info("[" + agents[2] + "] Received from [" + agents[0] + "]: " + body);
                return;
            }else if (agents[2].equals("any")){
                String recvUser = msgProperties.getConsumerQueue();
                userReceiveHandler.onGroupMessageArrive(recvUser, agents[1], body);
                logger.info("[" + recvUser + "] Received from group [" + agents[1] + "]: " + body);
                return;
            }
        }
        logger.error("Bad routing key format: " + msgProperties.getReceivedRoutingKey() + "Received: " + body);
    }


}
