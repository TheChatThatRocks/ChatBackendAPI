package com.eina.chat.backendapi.data.documental.dao;

import com.eina.chat.backendapi.data.documental.model.GroupMessageVo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupMessageDao extends MongoRepository<GroupMessageVo, String> {
    List<GroupMessageVo> getGroupMessageVosByGroupName(String groupName);

    void deleteGroupMessageVosByGroupName(String groupName);
}
