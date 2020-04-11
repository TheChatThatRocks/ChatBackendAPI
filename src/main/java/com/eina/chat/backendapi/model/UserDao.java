package com.eina.chat.backendapi.model;

import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<UserVo, String> {
    boolean existsByUsernameAndPassword(String username, String password);
}
