package com.eina.chat.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(
                // User
                "/sign-up", // Send sign up
                "/queue/error/sign-up", // Sign up errors
                "/message", // Send command to server
                "/queue/message", // Receive server response
                "/queue/error/message", // Send direct message errors
                // Admin
                "/queue/error/admin", // Admin receive server errors
                "/queue/topic/admin", // Admin receive server direct message
                "/admin" // Send admin commands
        );

        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
                //.withSockJS();
    }
}