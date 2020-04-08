package com.eina.chat.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
        // Authorization mapping
        messages.nullDestMatcher().permitAll()
                .simpSubscribeDestMatchers("/user/queue/error/sign-up").permitAll()
                .simpSubscribeDestMatchers("/user/queue/error/message").authenticated()
                .simpSubscribeDestMatchers("/user/queue/message").authenticated()
                .simpDestMatchers("/app/sign-up").permitAll()
                .simpDestMatchers("/app/message").authenticated()
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}