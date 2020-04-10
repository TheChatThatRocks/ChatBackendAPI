package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.CountDownLatch;

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

        final CountDownLatch step = new CountDownLatch(1);

        class UserListener implements MessageBrokerAPI.ReceiveHandler {
            @Override
            public void onUserMessageArrive(String user, String message) {
                step.countDown();
                System.out.printf("TEST -- [%s] Recibido msg: %s\n", user, message);
            }

            @Override
            public void onGroupMessageArrive(String user, String group, String message) {
            }
        }

        messageBrokerAPI.createUser("user2", new UserListener());
        messageBrokerAPI.createUser("user1", new UserListener());
        messageBrokerAPI.connectUser("user1");
        messageBrokerAPI.connectUser("user2");

        messageBrokerAPI.sendMessageToUser("user2", "user1", "hola 1");
        messageBrokerAPI.sendMessageToUser("user1", "user2", "hola 2");
        messageBrokerAPI.disconnectUser("user1");
        messageBrokerAPI.sendMessageToUser("user2", "user1", "hola 1 de nuevo");
        Thread.sleep(5000);
        System.out.println("Despertando...");
        messageBrokerAPI.connectUser("user1");
        messageBrokerAPI.disconnectUser("user1");
        messageBrokerAPI.disconnectUser("user2");
//        if (!messagesToReceive.await(300, TimeUnit.SECONDS)) {
//            fail("Message not received");
//        }
    }
}
