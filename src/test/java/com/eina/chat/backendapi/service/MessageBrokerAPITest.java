package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageBrokerAPITest {

    @Autowired
    MessageBrokerAPI messageBrokerAPI;

    private static class ReceiveHandlerTest implements ReceiveHandler {
        @Override
        public void onUserMessageArrive(String user, String message) {
            fail("Private message received");
        }

        @Override
        public void onGroupMessageArrive(String user, String group, String message) {
            fail("Group message received");
        }

        @Override
        public void onUserFileArrive(String username, byte[] file) {
            fail("Private file received");
        }

        @Override
        public void onGroupFileArrive(String username, String group, byte[] file) {
            fail("Group file received");
        }

        @Override
        public void onNotificationArrive(String content) {
            fail("Notification received");
        }
    }

    @BeforeEach
    public void setup() {
        messageBrokerAPI.deleteUser("user1");
        messageBrokerAPI.deleteUser("user2");
    }

    @AfterAll
    public void clean() {
        messageBrokerAPI.deleteUser("user1");
        messageBrokerAPI.deleteUser("user2");
        messageBrokerAPI.deleteUser("sender");
    }


    @Test
    public void sendMessageFromUserToUser() throws Exception {

        final CountDownLatch receiver1 = new CountDownLatch(2);
        final CountDownLatch receiver2 = new CountDownLatch(1);

        class UserListener extends ReceiveHandlerTest {
            @Override
            public void onUserMessageArrive(String user, String message) {
                if (user.equals("user1"))
                    receiver2.countDown();
                else if (user.equals("user2"))
                    receiver1.countDown();
                else fail("Unknown user");
            }
        }
        messageBrokerAPI.createUser("user2");
        messageBrokerAPI.createUser("user1");
        messageBrokerAPI.addUserReceiverMessagesCallback("user1", new UserListener());
        messageBrokerAPI.addUserReceiverMessagesCallback("user2", new UserListener());

        messageBrokerAPI.sendMessageToUser("user1", "user2", "hi 2");
        messageBrokerAPI.sendMessageToUser("user2", "user1", "hi 1");
        assert (receiver2.await(5, TimeUnit.SECONDS)): "User2 hasn't received message";

        // Check user 2 doesn't receive msg until it's connected
        messageBrokerAPI.deleteUserReceiverMessagesCallback("user1");
        messageBrokerAPI.sendMessageToUser("user2", "user1", "hi 1 again");
        assert (!receiver1.await(5, TimeUnit.SECONDS)): "User1 received message while disconnected";
        messageBrokerAPI.addUserReceiverMessagesCallback("user1", new UserListener());
        assert (receiver1.await(5, TimeUnit.SECONDS)): "User1 hasn't received some messages";
        messageBrokerAPI.deleteUserReceiverMessagesCallback("user1");
        messageBrokerAPI.deleteUserReceiverMessagesCallback("user2");
    }

    @Test
    public void sendMessageFromUserToGroup() throws Exception {

        final Semaphore group1 = new Semaphore(0);
        final Semaphore group2 = new Semaphore(0);
        final Semaphore user1 = new Semaphore(0);
        final Semaphore user2 = new Semaphore(0);

        class UserListener extends ReceiveHandlerTest {
            String userRecv;
            public UserListener(String user){
                this.userRecv = user;
            }
            @Override
            public void onGroupMessageArrive(String user, String group, String message){
                if (group.equals("group1")){
                    group1.release();
                }else if (group.equals("group2")){
                    group2.release();
                }else fail("Unknown group");
                if (user.equals("sender")&& userRecv.equals("user1") )
                    user1.release();
                else if (user.equals("sender") && userRecv.equals("user2") )
                    user2.release();
                else fail("Unknown user");
            }
        }

        messageBrokerAPI.createUser("user1");
        messageBrokerAPI.createUser("user2");
        messageBrokerAPI.createUser("sender");
        messageBrokerAPI.addUserReceiverMessagesCallback("user1", new UserListener("user1"));
        messageBrokerAPI.addUserReceiverMessagesCallback("user2", new UserListener("user2"));


        // Message sent to empty group doesn't arrive
        messageBrokerAPI.sendMessageToGroup("sender", "group1", "hi empty group");
        assert (!group1.tryAcquire( 5, TimeUnit.SECONDS)): "User1 or user2 have received group message and not added to group";

        // Group with 1 user
        messageBrokerAPI.addUserToGroup("user1", "group1");
        messageBrokerAPI.sendMessageToGroup("sender", "group1", "hi lone group");
        assert (group1.tryAcquire(5,  TimeUnit.SECONDS)): "group1 hasn't received 1st message";
        assert (user1.tryAcquire( 5, TimeUnit.SECONDS)): "User1 hasn't received 1st group message from group1";


        // Group with 2 users
        messageBrokerAPI.addUserToGroup("user2", "group1");
        messageBrokerAPI.sendMessageToGroup("sender", "group1", "hi dual group");
        assert (group1.tryAcquire(2, 5, TimeUnit.SECONDS)): "group1 hasn't received 2nd message";
        assert (user1.tryAcquire( 5, TimeUnit.SECONDS)): "User1 hasn't received 2nd group message from group1";
        assert (user2.tryAcquire( 5, TimeUnit.SECONDS)): "User2 hasn't received 1st group message from group1";


        messageBrokerAPI.addUserToGroup("user2", "group2");
        messageBrokerAPI.removeUserFromGroup("user2", "group1");
        messageBrokerAPI.sendMessageToGroup("sender", "group1", "hi lone group1 agian");
        messageBrokerAPI.sendMessageToGroup("sender", "group2", "hi lone group2");
        assert (group1.tryAcquire( 5, TimeUnit.SECONDS)): "group1 hasn't received 2nd message";
        assert (group2.tryAcquire( 5, TimeUnit.SECONDS)): "group1 hasn't received 1st message";
        assert (user1.tryAcquire( 5, TimeUnit.SECONDS)): "User1 hasn't received 2nd group message from group1";
        assert (user2.tryAcquire( 5, TimeUnit.SECONDS)): "User2 hasn't received 1st group message from group1";

        messageBrokerAPI.removeUserFromGroup("user1", "group1");
        messageBrokerAPI.removeUserFromGroup("user2", "group2");

        // Message from empty group doesn't arrive
        messageBrokerAPI.sendMessageToGroup("sender", "group1", "hi empty group1");
        messageBrokerAPI.sendMessageToGroup("sender", "group1", "hi empty group2");
        assert (!group1.tryAcquire( 5, TimeUnit.SECONDS) && !group2.tryAcquire( 5, TimeUnit.SECONDS)): "neither user1 nor user2" +
                " received message from empty group";

        // Message to not empty group arrive
        messageBrokerAPI.addUserToGroup("user2", "group1");
        messageBrokerAPI.addUserToGroup("user1", "group2");
        messageBrokerAPI.sendMessageToGroup("sender", "group1", "hi user2 on group1");
        assert (user2.tryAcquire(5, TimeUnit.SECONDS)): "User2 not received msg from re-populated group1";
        messageBrokerAPI.sendMessageToGroup("sender", "group2", "hi user1 on group2");
        assert (user1.tryAcquire(5, TimeUnit.SECONDS)): "User1 not received msg from re-populated group2";

        messageBrokerAPI.deleteUserReceiverMessagesCallback("user1");
        messageBrokerAPI.deleteUserReceiverMessagesCallback("user2");

    }

    @Test
    public void sendFileFromUserToUser() throws Exception {
        final Semaphore user1 = new Semaphore(0);
        final Semaphore user2 = new Semaphore(0);


        byte[] fileToSend = ("Hi!\nDon't be scared by what I'm gonna tell you: this\n" +
                "is the best chat engine ever created. It will keep mankind\n" +
                "socked for the coming centuries...").getBytes();

        class UserListener extends ReceiveHandlerTest {
            @Override
            public void onUserFileArrive(String username, byte[] file) {
                if (username.equals("user1"))
                    user2.release();
                else if (username.equals("user2"))
                    user1.release();
                else fail("Unknown user");
                assert (Arrays.equals(file, fileToSend)): "File content has changed";
            }
        }

        messageBrokerAPI.createUser("user2");
        messageBrokerAPI.createUser("user1");
        messageBrokerAPI.addUserReceiverMessagesCallback("user1", new UserListener());
        messageBrokerAPI.addUserReceiverMessagesCallback("user2", new UserListener());

        messageBrokerAPI.sendFileToUser("user1", "user2", fileToSend);
        assert (user2.tryAcquire(5, TimeUnit.SECONDS)): "User 2 hasn't received message";
        messageBrokerAPI.sendFileToUser("user2", "user1", fileToSend);
        assert (user1.tryAcquire(5, TimeUnit.SECONDS)): "User 1 hasn't received message";

        // Check user 2 doesn't receive msg until it's connected
        messageBrokerAPI.deleteUserReceiverMessagesCallback("user1");
        messageBrokerAPI.sendFileToUser("user2", "user1", fileToSend);
        assert (!user1.tryAcquire(5, TimeUnit.SECONDS)): "User1 received message while disconnected";
        messageBrokerAPI.addUserReceiverMessagesCallback("user1", new UserListener());
        assert (user1.tryAcquire(5, TimeUnit.SECONDS)): "User1 hasn't received some messages";
        messageBrokerAPI.deleteUserReceiverMessagesCallback("user1");
        messageBrokerAPI.deleteUserReceiverMessagesCallback("user2");
    }


    @Test
    public void sendFileFromUserToGroup() throws Exception {
        final Semaphore group1 = new Semaphore(0);
        final Semaphore group2 = new Semaphore(0);
        final Semaphore user1 = new Semaphore(0);
        final Semaphore user2 = new Semaphore(0);


        byte[] fileToSend = ("Hi!\nDon't be scared by what I'm gonna tell you: this\n" +
                            "is the best chat engine ever created. It will keep mankind\n" +
                            "socked for the coming centuries...").getBytes();

        class UserListener extends ReceiveHandlerTest {

            String userRecv;
            public UserListener(String user){
                this.userRecv = user;
            }

            @Override
            public void onGroupFileArrive(String user, String group, byte[] file) {
                if (group.equals("group1")){
                    group1.release();
                }else if (group.equals("group2")){
                    group2.release();
                }else fail("Unknown group");
                if (user.equals("sender")&& userRecv.equals("user1") )
                    user1.release();
                else if (user.equals("sender") && userRecv.equals("user2") )
                    user2.release();
                else fail("Unknown user");
                assert (Arrays.equals(file, fileToSend)): "File content has changed";
            }
        }

        messageBrokerAPI.createUser("user1");
        messageBrokerAPI.createUser("user2");
        messageBrokerAPI.createUser("sender");
        messageBrokerAPI.addUserReceiverMessagesCallback("user1", new UserListener("user1"));
        messageBrokerAPI.addUserReceiverMessagesCallback("user2", new UserListener("user2"));


        // Message sent to empty group doesn't arrive
        messageBrokerAPI.sendFileToGroup("sender", "group1", fileToSend);
        assert (!group1.tryAcquire( 10, TimeUnit.SECONDS)): "User1 or user2 have received group file and not added to group";


        // Group with 2 users
        messageBrokerAPI.addUserToGroup("user2", "group1");
        messageBrokerAPI.addUserToGroup("user1", "group1");
        messageBrokerAPI.sendFileToGroup("sender", "group1", fileToSend);
        assert (group1.tryAcquire(2, 5, TimeUnit.SECONDS)): "group1 hasn't received 2nd message";
        assert (user1.tryAcquire( 5, TimeUnit.SECONDS)): "User1 hasn't received 2nd group message from group1";
        assert (user2.tryAcquire( 5, TimeUnit.SECONDS)): "User2 hasn't received 1st group message from group1";
    }
}
