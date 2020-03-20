package com.eina.chat.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
        System.out.println("Inside configure in bound --------------------------");
        // Authorization mapping
        messages.nullDestMatcher().permitAll()
                .simpSubscribeDestMatchers("/user/**").authenticated()//.hasRole("USER")
                .simpDestMatchers("/app/**").authenticated()
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}