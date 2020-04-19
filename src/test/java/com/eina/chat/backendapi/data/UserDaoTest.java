package com.eina.chat.backendapi.data;

import com.eina.chat.backendapi.security.AccessLevels;
import com.eina.chat.backendapi.service.PersistentDataAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserDaoTest {
    // User database service
    @Autowired
    private PersistentDataAPI persistentDataAPI;

    // Variables
    final private String nameUser1 = "testUser1";
    final private String nameUser2 = "testUser2";

    final private String passUser1 = "test";
    final private String passUser2 = "test";

    final private String roomName = "testroom";

    @Test
    public void deletingAdminAccountWithoutDeletingGroupFirst() {
        // Create users in database
        persistentDataAPI.createUser(nameUser1, passUser1, AccessLevels.ROLE_USER);
        persistentDataAPI.createUser(nameUser2, passUser2, AccessLevels.ROLE_USER);

        // Create room
        persistentDataAPI.createGroup(nameUser1, roomName);

        // Delete admin account
        persistentDataAPI.deleteUser(nameUser1);

        // Delete member account
        persistentDataAPI.deleteUser(nameUser2);
    }
}
