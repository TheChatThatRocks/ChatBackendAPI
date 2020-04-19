package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.data.relational.dao.UserDao;
import com.eina.chat.backendapi.data.relational.model.UserVo;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountDatabaseAPI {
    /**
     * User database
     */
    @Autowired
    private UserDao userDao;

    /**
     * Create user if doesn't exist yet
     *
     * @param username          user username
     * @param encryptedPassword encrypted user password
     * @param role              user role
     */
    public void createUser(@NonNull String username, @NonNull String encryptedPassword, @NonNull String role) {
        if (!userDao.existsById(username)) {
            userDao.save(new UserVo(username, encryptedPassword, role));
        }
    }

    /**
     * Delete user
     *
     * @param username user username
     */
    public void deleteUser(@NonNull String username) {
        if (userDao.existsById(username))
            userDao.deleteById(username);
    }

    /**
     * Check if user exist
     *
     * @param username user username
     * @return true if user exist, false otherwise
     */
    public boolean checkUserExist(@NonNull String username) {
        return userDao.existsById(username);
    }

    /**
     * Check if user credentials match
     *
     * @param username          user username
     * @param encryptedPassword encrypted user password
     * @return true if user exist and match, false otherwise
     */
    public boolean checkUserCredentials(@NonNull String username, @NonNull String encryptedPassword) {
        return userDao.existsByUsernameAndPassword(username, encryptedPassword);
    }

    /**
     * Get role of user
     *
     * @param username user username
     * @return user role if user exist, null otherwise
     */
    public String getUserRole(@NonNull String username) {
        Optional<UserVo> userVo = userDao.findById(username);
        return userVo.map(UserVo::getRole).orElse(null);
    }
}
