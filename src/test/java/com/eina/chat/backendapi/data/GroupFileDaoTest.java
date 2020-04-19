package com.eina.chat.backendapi.data;

import com.eina.chat.backendapi.data.documental.dao.GroupFileDao;
import com.eina.chat.backendapi.data.documental.dao.GroupMessageDao;
import com.eina.chat.backendapi.data.documental.model.GroupFileVo;
import com.eina.chat.backendapi.data.documental.model.GroupMessageVo;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupFileDaoTest {
    // Message database service
    @Autowired
    private GroupFileDao groupFileDao;

    @Test
    public void addingGroupFilesToDatabase() {
        byte[] savedFileContent = RandomUtils.nextBytes(100);

        List<GroupFileVo> groupFileVoList = Arrays.asList(
                new GroupFileVo("12", "12", "Prueba12", savedFileContent),
                new GroupFileVo("13", "12", "Prueba13", savedFileContent),
                new GroupFileVo("14", "12", "Prueba14", savedFileContent),
                new GroupFileVo("15", "12", "Prueba15", savedFileContent),
                new GroupFileVo("16", "12", "Prueba16", savedFileContent)
        );

        groupFileDao.insert(groupFileVoList);

        List<GroupFileVo> retrievedFiles = groupFileDao.getGroupFileVosByGroupName("12");

        assert (groupFileVoList.size() == retrievedFiles.size());

        groupFileDao.deleteGroupFileVosByGroupName("12");

        retrievedFiles = groupFileDao.getGroupFileVosByGroupName("12");

        assert (retrievedFiles.size() == 0);
    }
}
