package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.model.User;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDatabaseAPI {
    /**
     * Create user if doesn't exist yet
     * @param user user to add
     * @return true if user doesn't exist yet, false otherwise
     */
    public boolean createUser(@NonNull User user){
        // TODO:
        return true;
    }

    /**
     * Delete user
     * @param username username of the user to delete
     */
    public void deleteUser(@NonNull String username){
        // TODO:
    }

    /**
     * Check if user exist
     * @param username username of the user to check
     * @return true if user exist, false otherwise
     */
    public boolean checkUserExist(@NonNull String username){
        // TODO:
        return true;
    }

    /**
     * Check if username and credentials match
     * @param user user to check
     * @return true if match, false otherwise
     */
    public boolean checkUserCredentials(@NonNull User user){
        // TODO:
        return true;
    }
}
