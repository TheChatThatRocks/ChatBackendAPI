package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.errors.WSResponseStatus;
import com.eina.chat.backendapi.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        int user1_callback_id = messageBrokerAPI.addUserReceiverMessagesCallback(new MessageBrokerAPI.BrokerMessagePackage() {
            @Override
            public void onUserMessageArrive(String user, String message) {
                System.out.println("Ha llegado el mensaje");
                messagesToReceive.countDown();
            }

            @Override
            public void onGroupMessageArrive(String user, String group, String message) {

            }
        }, "user1");

        int user2_callback_id = messageBrokerAPI.addUserReceiverMessagesCallback(new MessageBrokerAPI.BrokerMessagePackage() {
            @Override
            public void onUserMessageArrive(String user, String message) {

            }

            @Override
            public void onGroupMessageArrive(String user, String group, String message) {

            }
        }, "user2");

        messageBrokerAPI.sendMessageToUser("user2", "user1", "message");

        if (!messagesToReceive.await(10, TimeUnit.SECONDS)) {
            fail("Message not received");
        }

        messageBrokerAPI.deleteUserReceiverMessagesCallback(user1_callback_id);
        messageBrokerAPI.deleteUserReceiverMessagesCallback(user2_callback_id);
    }
}
