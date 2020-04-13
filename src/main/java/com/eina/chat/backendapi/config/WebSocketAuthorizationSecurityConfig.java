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
                // User
                .simpSubscribeDestMatchers("/user/queue/error/sign-up").permitAll()
                .simpSubscribeDestMatchers("/user/queue/error/message").hasAuthority(AccessLevels.ROLE_USER)
                .simpSubscribeDestMatchers("/user/queue/message").hasAuthority(AccessLevels.ROLE_USER)
                .simpDestMatchers("/app/sign-up").permitAll()
                .simpDestMatchers("/app/message").hasAuthority(AccessLevels.ROLE_USER)
                // Admin
                .simpSubscribeDestMatchers("/user/queue/topic/admin").hasAuthority(AccessLevels.ROLE_ADMIN)
                .simpSubscribeDestMatchers("/user/queue/error/admin").hasAuthority(AccessLevels.ROLE_ADMIN)
                .simpDestMatchers("/app/admin").hasAuthority(AccessLevels.ROLE_ADMIN)
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}