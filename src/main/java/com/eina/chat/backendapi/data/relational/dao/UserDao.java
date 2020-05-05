package com.eina.chat.backendapi.data.relational.dao;

import com.eina.chat.backendapi.data.relational.model.UserVo;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<UserVo, String> {
    boolean existsByUsernameAndPassword(String username, String password);
}
