package com.eina.chat.backendapi.data.relational.dao;

import com.eina.chat.backendapi.data.relational.model.GroupVo;
import com.eina.chat.backendapi.data.relational.model.UserVo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupDao extends CrudRepository<GroupVo, String> {
    List<GroupVo> findByAdmin(UserVo userVo);
}
