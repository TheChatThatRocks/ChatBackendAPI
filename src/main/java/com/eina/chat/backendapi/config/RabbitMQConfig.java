package com.eina.chat.backendapi.config;

import com.eina.chat.backendapi.rabbitmq.*;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Configuration
public class RabbitMQConfig {
    @Bean
    public static Logger logger() {
        return LogManager.getLogger("rabbit");
    }

    @Bean
    @Autowired
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    @Autowired
    public TopicExchange topic(RabbitAdmin rabbitAdmin) {
        final TopicExchange myExchange = new TopicExchange("user.topic", true, false);
        rabbitAdmin.declareExchange(myExchange);
        return myExchange;
    }

    @Bean
    @Autowired
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, TopicExchange topicExchange) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(topicExchange.getName());
        return rabbitTemplate;
    }

    @Bean
    public MessageBrokerAPI brokerAPI() {
        return new MessageBrokerAPI();
    }

    @Bean
    public ConsumersContainer consumerListeners() {
        return new ConsumersContainer();
    }

    @Bean
    public Producer sender() {
        return new Producer();
    }

}
