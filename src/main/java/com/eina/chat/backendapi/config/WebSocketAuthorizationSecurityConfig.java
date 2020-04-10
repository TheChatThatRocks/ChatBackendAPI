package com.eina.chat.backendapi.config;

import com.eina.chat.backendapi.security.AccessLevels;
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
                .simpSubscribeDestMatchers("/user/queue/error/message").hasAuthority(AccessLevels.ROLE_USER)
                .simpSubscribeDestMatchers("/user/queue/message").hasAuthority(AccessLevels.ROLE_USER)
                .simpDestMatchers("/app/sign-up").permitAll()
                .simpDestMatchers("/app/message").hasAuthority(AccessLevels.ROLE_USER)
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}