package com.eina.chat.backendapi.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupDao extends CrudRepository<GroupVo, String> {
    List<GroupVo> findByAdmin(UserVo userVo);
}
