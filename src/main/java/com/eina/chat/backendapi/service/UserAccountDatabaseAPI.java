package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.security.AccessLevels;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDatabaseAPI {
    /**
     * Create user if doesn't exist yet
     *
     * @param username          user username
     * @param encryptedPassword encrypted user password
     * @param role              user role
     */
    public void createUser(@NonNull String username, @NonNull String encryptedPassword, @NonNull String role) {
        // TODO:
    }

    /**
     * Delete user
     *
     * @param username user username
     */
    public void deleteUser(@NonNull String username) {
        // TODO:
    }

    /**
     * Check if user exist
     *
     * @param username user username
     * @return true if user exist, false otherwise
     */
    public boolean checkUserExist(@NonNull String username) {
        // TODO:
        return true;
    }

    /**
     * Check if user credentials match
     *
     * @param username          user username
     * @param encryptedPassword encrypted user password
     * @return true if user exist and match, false otherwise
     */
    public boolean checkUserCredentials(@NonNull String username, @NonNull String encryptedPassword) {
        // TODO:
        return true;
    }

    /**
     * Get role of user
     * @param username user username
     * @return user role if user exist, null otherwise
     */
    public String getUserRole(@NonNull String username){
        // TODO:
        return AccessLevels.ROLE_USER;
    }

//    /**
//     * Get all users whose name contains the username provided
//     * @param username user username
//     * @return all match
//     */
//    public List<String> searchUsername(@NonNull String username){
//        // TODO:
//        return new ArrayList<>();
//    }
}
