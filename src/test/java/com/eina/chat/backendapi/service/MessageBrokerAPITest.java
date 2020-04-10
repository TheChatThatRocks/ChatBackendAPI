package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageBrokerAPITest {

    @Autowired
    MessageBrokerAPI messageBrokerAPI;

    @BeforeAll
    public void setup() {
    }


    @Test
    public void sendMessageFromUserToUser() throws Exception {

        final CountDownLatch receiver1 = new CountDownLatch(2);
        final CountDownLatch receiver2 = new CountDownLatch(1);

        class UserListener implements ReceiveHandler {
            @Override
            public void onUserMessageArrive(String user, String message) {
                if (user.equals("user1"))
                    receiver1.countDown();
                else if (user.equals("user2"))
                    receiver2.countDown();
            }

            @Override
            public void onGroupMessageArrive(String user, String group, String message) {
            }

            @Override
            public void onUserFileArrive(String username, byte[] file) {

            }

            @Override
            public void onGroupFileArrive(String username, String group, byte[] file) {

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
}
