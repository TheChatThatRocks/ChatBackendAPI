package com.eina.chat.backendapi.rabbitmq;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class Producer {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Exchange topic;


    public Producer() {
    }

    /***
     * Send message to the topic exchange
     * @param key topic use it to bind the msg to a queue. Format: from.group.to.
     *            For private msg group should be any and for group msg user is any.
     * @param message content of message
     */
    public void sendMessage(String key, String message) {
        MessageProperties msgProp = new MessageProperties();
        msgProp.setContentType("TEXT");
        template.convertAndSend(key, MessageBuilder.withBody(message.getBytes()).andProperties(msgProp).build());
    }

    /**
     * Send file to the topic exchange
     *
     * @param key  topic use it to bind the msg to a queue. Format: from.group.to.
     *             For private msg group should be any and for group msg user is any.
     * @param file content of file
     */
    public void sendFile(String key, byte[] file) {
        MessageProperties msgProp = new MessageProperties();
        msgProp.setContentType("FILE");
        template.convertAndSend(topic.getName(), key, MessageBuilder.withBody(file).andProperties(msgProp).build());
    }
}
