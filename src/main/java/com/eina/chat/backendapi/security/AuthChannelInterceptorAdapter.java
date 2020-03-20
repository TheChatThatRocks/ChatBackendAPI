package com.eina.chat.backendapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {
    private static final String USERNAME_HEADER = "login";
    private static final String PASSWORD_HEADER = "passcode";

    @Autowired
    WebSocketAuthenticatorService webSocketAuthenticatorService;

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) throws AuthenticationException {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == accessor.getCommand()) {

            final String username = accessor.getFirstNativeHeader(USERNAME_HEADER);

            // TODO: Password siempre aparece como protected
            final String password = accessor.getFirstNativeHeader(PASSWORD_HEADER);

            System.out.println("Credentials obtained: " + username + "|n|" + password + "|n");

            final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFail(username, password);

            if(user.isAuthenticated()){
                System.out.println("Esta autenticado --------------------------------------------");
            }else{
                System.out.println("No Esta autenticado --------------------------------------------");
            }
            accessor.setUser(user);
        }
        return message;
    }
}
