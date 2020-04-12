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
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.min;

public class Consumer implements MessageListener{
    private final byte MAX_LEN_FILE_CONTENT_LOG = 25;

    private static final Logger logger = LogManager.getLogger("rabbit");
    private final ReceiveHandler userReceiveHandler;

    public Consumer(ReceiveHandler apiHandlerMessage){
        super();
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
        MessageProperties msgProperties = message.getMessageProperties();
        String[] agents = msgProperties.getReceivedRoutingKey().split("\\.", 3);
        if (agents.length == 3){
            if (msgProperties.getContentType().equals("TEXT")){
                String body = new String(message.getBody());
                if (agents[1].equals("any")){// It's a private msg
                    userReceiveHandler.onUserMessageArrive(agents[0], body);
                    logger.info("[" + agents[2] + "] Received from [" + agents[0] + "]: " + body);
                    return;
                }else if (agents[2].equals("any")){
                    String recvUser = msgProperties.getConsumerQueue();
                    userReceiveHandler.onGroupMessageArrive(agents[0], agents[1], body);
                    logger.info("[" + recvUser + "] Received from group [" + agents[1] + "]: " + body);
                    return;
                }
                logger.error("Bad routing key format: " + msgProperties.getReceivedRoutingKey() +
                        "Message received: " + body);
            }
            else if (msgProperties.getContentType().equals("FILE")) {
                byte[] body = message.getBody();
                String introFile = new String(body, 0, min(body.length, MAX_LEN_FILE_CONTENT_LOG)) + "...";
                if (agents[1].equals("any")){// It's a private msg
                    userReceiveHandler.onUserFileArrive(agents[0], body);
                    logger.info("[" + agents[2] + "] Received from [" + agents[0] + "]: " + introFile );
                    return;
                }else if (agents[2].equals("any")){
                    String recvUser = msgProperties.getConsumerQueue();
                    userReceiveHandler.onGroupFileArrive(agents[0], agents[1], body);
                    logger.info("[" + recvUser + "] Received from group [" + agents[1] + "]: " + introFile );
                    return;
                }
                logger.error("Bad routing key format: " + msgProperties.getReceivedRoutingKey() +
                        "File received: " + introFile);
            }

        }
    }


}
