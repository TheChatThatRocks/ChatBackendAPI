package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

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
        messageBrokerAPI.createUser("user1");
        messageBrokerAPI.createUser("user2");

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        messageBrokerAPI.addUserReceiverMessagesCallback("user1", new ReceiveHandler() {
            @Override
            public void onUserMessageArrive(String user, String message) {
                System.out.println("Ha llegado el mensaje");
                messagesToReceive.countDown();
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
        });


        messageBrokerAPI.addUserReceiverMessagesCallback("user2", new ReceiveHandler() {
            @Override
            public void onUserMessageArrive(String user, String message) {

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
        });


        messageBrokerAPI.sendMessageToUser("user2", "user1", "message");

        if (!messagesToReceive.await(10, TimeUnit.SECONDS)) {
            fail("Message not received");
        }

        messageBrokerAPI.deleteUserReceiverMessagesCallback("user1");
        messageBrokerAPI.deleteUserReceiverMessagesCallback("user2");
    }
}
