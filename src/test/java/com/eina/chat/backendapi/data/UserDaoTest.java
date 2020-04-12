package com.eina.chat.backendapi.data;

import com.eina.chat.backendapi.security.AccessLevels;
import com.eina.chat.backendapi.service.GroupsManagementDatabaseAPI;
import com.eina.chat.backendapi.service.UserAccountDatabaseAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserDaoTest {
    // User database service
    @Autowired
    private UserAccountDatabaseAPI userAccountDatabaseAPI;

    @Autowired
    private GroupsManagementDatabaseAPI groupsManagementDatabaseAPI;

    // Variables
    final private String nameUser1 = "testUser1";
    final private String nameUser2 = "testUser2";

    final private String passUser1 = "test";
    final private String passUser2 = "test";

    final private String roomName = "testroom";

    @Test
    public void deletingAdminAccountWithoutDeletingGroupFirst() {
        // Create users in database
        userAccountDatabaseAPI.createUser(nameUser1, passUser1, AccessLevels.ROLE_USER);
        userAccountDatabaseAPI.createUser(nameUser2, passUser2, AccessLevels.ROLE_USER);

        // Create room
        groupsManagementDatabaseAPI.createGroup(nameUser1, roomName);

        // Delete admin account
        userAccountDatabaseAPI.deleteUser(nameUser1);

        // Delete member account
        userAccountDatabaseAPI.deleteUser(nameUser2);
    }
}
