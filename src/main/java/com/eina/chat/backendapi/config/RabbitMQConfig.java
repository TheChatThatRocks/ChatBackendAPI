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
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitMQConfig {
//
//    @Value("${spring.rabbitmq.host}")
//    private String host;
//
//    @Value("${spring.rabbitmq.username}")
//    private String username;
//
//    @Value("${spring.rabbitmq.password}")
//    private String pass;
//
//    @Value("${spring.rabbitmq.port}")
//    private int port;
//
//    @Value("${spring.rabbitmq.addresses}")
//    private String url;

//    @Bean
//    @Profile("devDocker")
//    public ConnectionFactory connectionFactoryDev() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        try {
//            connectionFactory.setHost(host);
//            connectionFactory.setPort(port);
//            connectionFactory.setUsername(username);
//            connectionFactory.setPassword(pass);
//            connectionFactory.setConnectionNameStrategy(connectionFactory1 -> "Chat-API");
//            System.out.println(" [*] AQMP broker CONNECTED TO: " + host);
//        } catch (Exception e) {
//            System.out.println(" [*] AQMP broker NOT found in " + host);
//            System.exit(-1);
//        }
//        return connectionFactory;
//    }


//    @Bean
//    @Profile("prod")
//    public ConnectionFactory connectionFactoryProd() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        try {
//            connectionFactory.setUri(url);
//            connectionFactory.setConnectionNameStrategy(connectionFactory1 -> "Chat-API");
//            System.out.println(" [*] AQMP broker CONNECTED TO: " + host);
//        } catch (Exception e) {
//            System.out.println(" [*] AQMP broker NOT found in " + host);
//            System.exit(-1);
//        }
//        return connectionFactory;
//    }


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
