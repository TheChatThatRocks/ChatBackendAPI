package com.eina.chat.backendapi.data.documental.dao;

import com.eina.chat.backendapi.data.documental.model.GroupFileVo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupFileDao extends MongoRepository<GroupFileVo, String> {
    List<GroupFileVo> getGroupFileVosByGroupName(String groupName);

    void deleteGroupFileVosByGroupName(String groupName);
}
