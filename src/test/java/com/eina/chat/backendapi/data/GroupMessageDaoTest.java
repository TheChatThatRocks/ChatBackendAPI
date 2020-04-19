package com.eina.chat.backendapi.data;

import com.eina.chat.backendapi.data.documental.dao.GroupMessageDao;
import com.eina.chat.backendapi.data.documental.model.GroupMessageVo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupMessageDaoTest {
    // Message database service
    @Autowired
    private GroupMessageDao messageDao;

    @Test
    public void addingMessageToDatabase() {
        List<GroupMessageVo> messageVoList = Arrays.asList(
                new GroupMessageVo("12", "12", "Prueba12"),
                new GroupMessageVo("13", "12", "Prueba13"),
                new GroupMessageVo("14", "12", "Prueba14"),
                new GroupMessageVo("15", "12", "Prueba15"),
                new GroupMessageVo("16", "12", "Prueba16")
        );
        messageDao.insert(messageVoList);

        List<GroupMessageVo> retrievedMessages = messageDao.getGroupMessageVosByGroupName("12");

        assert (messageVoList.size() == retrievedMessages.size());

        messageDao.deleteGroupMessageVosByGroupName("12");

        retrievedMessages = messageDao.getGroupMessageVosByGroupName("12");

        assert (retrievedMessages.size() == 0);
    }
}
