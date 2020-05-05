package com.eina.chat.backendapi.config;

import com.eina.chat.backendapi.rabbitmq.*;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jca.endpoint.AbstractMessageEndpointFactory;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.addresses:amqp://localhost}")
    private String amqpURL;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        try {
            connectionFactory.setUri(amqpURL);
            // TODO: ajustar canales, colas... según capacidades del cloud
            // connectionFactory.setChannelCacheSize(40);
            connectionFactory.setConnectionNameStrategy(connectionFactory1 -> "Chat-API");
        } catch (Exception e) {
            System.out.println(" [*] AQMP broker not found in " + amqpURL);
            System.exit(-1);
        }
        System.out.println(" [*] AQMP broker CONNECTED TO: " + amqpURL);
        return connectionFactory;
    }


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
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, TopicExchange topicExchange) { //, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(jsonMessageConverter());
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
