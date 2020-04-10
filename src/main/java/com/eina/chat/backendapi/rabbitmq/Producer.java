package com.eina.chat.backendapi.rabbitmq;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class Producer {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Exchange topic;


    public Producer(){}

    /***
     * Send message to the topic exchange
     * @param key topic use it to bind the msg to a queue. Format: from.group.to.
     *            For private msg group should be any and for group msg user is any.
     * @param message content of message
     */
    public void send(String key, String message) {
        template.convertAndSend(topic.getName(), key, message);
    }
}
