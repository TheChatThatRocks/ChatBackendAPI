package com.eina.chat.backendapi.config;

import com.eina.chat.backendapi.rabbitmq.*;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        } catch (Exception e) {
            System.out.println(" [*] AQMP broker not found in " + amqpURL);
            System.exit(-1);
        }
        System.out.println(" [*] AQMP broker CONNECTED TO: " + amqpURL);
        return connectionFactory;
    }


    @Bean
    public static Logger logger(){
        return LogManager.getLogger("rabbit");
    }

    @Bean
    @Autowired
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){ //, MessageConverter messageConverter) {
        //        rabbitTemplate.setMessageConverter(messageConverter);
        return new RabbitTemplate(connectionFactory);
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
    public MessageBrokerAPI brokerAPI(){
        return new MessageBrokerAPI();
    }

    @Bean
    public ConsumersContainer consumers(){
        return new ConsumersContainer();
    }

    @Bean
    public Producer sender(){
        return new Producer();
    }

}
