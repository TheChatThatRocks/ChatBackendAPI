package com.eina.chat.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

//@Configuration
// TODO: Solucionar, el problema tiene que estar en esta clase
public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
        System.out.println("Inside configure in bound --------------------------");
        // Authorization mapping
        messages.simpDestMatchers("/app/**").hasRole("USER")
                .simpSubscribeDestMatchers("/user/**").hasRole("USER")
                .anyMessage().denyAll();
    }
}