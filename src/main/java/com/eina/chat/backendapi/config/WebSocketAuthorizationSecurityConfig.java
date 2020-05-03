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
                // All
                .simpSubscribeDestMatchers("/user/queue/error/sign-up").permitAll()
                .simpDestMatchers("/app/sign-up").permitAll()

                // Authenticated
                .simpSubscribeDestMatchers("/user/queue/error/auth-level").authenticated()
                .simpSubscribeDestMatchers("/user/queue/auth-level").authenticated()
                .simpDestMatchers("/app/auth-level").authenticated()

                // User
                .simpSubscribeDestMatchers("/user/queue/error/message").hasAuthority(AccessLevels.ROLE_USER)
                .simpSubscribeDestMatchers("/user/queue/message").hasAuthority(AccessLevels.ROLE_USER)
                .simpDestMatchers("/app/message").hasAuthority(AccessLevels.ROLE_USER)

                // Admin
                .simpSubscribeDestMatchers("/user/queue/error/admin").hasAuthority(AccessLevels.ROLE_ADMIN)
                .simpDestMatchers("/app/admin").hasAuthority(AccessLevels.ROLE_ADMIN)

                // Other
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}