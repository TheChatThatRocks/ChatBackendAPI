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
                // All
                "/queue/error/sign-up", // Sign up errors
                "/sign-up", // Send sign up

                // Authenticated
                "/queue/error/auth-level", // Auth level error
                "/queue/auth-level", // Receive auth level response
                "/auth-level", // Send auth level

                // User
                "/queue/error/message", // Send direct message errors
                "/queue/message", // Receive server response
                "/message", // Send command to server

                // Admin
                "/queue/error/admin", // Admin receive server errors
                "/admin" // Send admin commands
        );

        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
    }
}