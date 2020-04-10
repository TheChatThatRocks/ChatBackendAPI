package com.eina.chat.backendapi.security;

import com.eina.chat.backendapi.model.User;
import com.eina.chat.backendapi.service.EncryptionAPI;
import com.eina.chat.backendapi.service.UserAccountDatabaseAPI;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class WebSocketAuthenticatorService {
    /**
     * User account database API
     */
    @Autowired
    private UserAccountDatabaseAPI userAccountDatabaseAPI;

    /**
     * Encryption API
     */
    @Autowired
    private EncryptionAPI encryptionAPI;

    public UsernamePasswordAuthenticationToken getAuthenticationToken(@NonNull String username, @NonNull String password) throws AuthenticationException {
        if (userAccountDatabaseAPI.checkUserCredentials(username, encryptionAPI.asymmetricEncryptString(password))) {
            // Null credentials
            return new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.singleton((GrantedAuthority) () -> userAccountDatabaseAPI.getUserRole(username))
            );
        } else
            throw new BadCredentialsException("Bad credentials for user " + username);
    }
}
