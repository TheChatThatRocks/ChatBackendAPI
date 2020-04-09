package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.model.User;
import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.signup.request.AddAccountCommand;
import com.eina.chat.backendapi.protocol.packages.signup.response.SignUpErrorResponse;
import com.eina.chat.backendapi.protocol.packages.signup.response.SignUpSuccessResponse;
import com.eina.chat.backendapi.service.UserAccountDatabaseAPI;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SignUpControllerTest {
    @LocalServerPort
    private int port;

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-api-ws-uri:}")
    private String backEndURI;

    // User database service
    @Autowired
    private UserAccountDatabaseAPI userAccountDatabaseAPI;

    // Test user data
    final private String username = "testusername";
    final private String password = "testpassword";

    @BeforeEach
    public void setupForEach() {
        if (userAccountDatabaseAPI.checkUserExist(username)) {
            userAccountDatabaseAPI.deleteUser(username);
        }
    }

    @AfterEach
    public void deleteForEach() {
        if (userAccountDatabaseAPI.checkUserExist(username)) {
            userAccountDatabaseAPI.deleteUser(username);
        }
    }

    /**
     * Test signing up a new user
     */
    @Test
    public void newUser() throws Exception {
        // Connection variables
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(standardWebSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Failure variable
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        // Message id
        final int messageId = 1;

        // New user
        AddAccountCommand sendCommandPackage = new AddAccountCommand(messageId, username, password);

        StompSession session = stompClient.connect("ws://" + backEndURI + ":{port}/ws", new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);

        // Check if connection have failed
        assert (session != null && session.isConnected());

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        // Subscribe
        session.subscribe("/user/queue/error/sign-up", new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                BasicPackage errorResponse = (BasicPackage) payload;
                if (errorResponse.getMessageId() == messageId && errorResponse instanceof SignUpSuccessResponse)
                    messagesToReceive.countDown();
                else {
                    failure.set(new Exception("Unexpected message received or sign-up fail"));
                }
            }
        });

        session.send("/app/sign-up", sendCommandPackage);

        boolean hasReceivedMessage = messagesToReceive.await(5, TimeUnit.SECONDS);

        session.disconnect();

        if (failure.get() != null) {
            fail(failure.get().getMessage());
        } else if (!hasReceivedMessage) {
            fail("Test wasn't completed");
        }
    }

    /**
     * Test signing up a user that exist yet
     */
    @Test
    public void duplicatedUser() throws Exception {
        // Create user
        if (!userAccountDatabaseAPI.checkUserExist(username)) {
            userAccountDatabaseAPI.createUser(new User(username, password));
        }

        // Connection variables
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(standardWebSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Failure variable
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        // Message id
        final int messageId = 1;

        // New user
        AddAccountCommand sendCommandPackage = new AddAccountCommand(messageId, username, password);

        StompSession session = stompClient.connect("ws://" + backEndURI + ":{port}/ws", new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);

        // Check if connection have failed
        assert (session != null && session.isConnected());

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        // Subscribe
        session.subscribe("/user/queue/error/sign-up", new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                BasicPackage errorResponse = (BasicPackage) payload;
                if (errorResponse.getMessageId() == messageId && errorResponse instanceof SignUpErrorResponse)
                    messagesToReceive.countDown();
                else {
                    failure.set(new Exception("Unexpected message received or sign-up fail"));
                }
            }
        });

        session.send("/app/sign-up", sendCommandPackage);

        boolean hasReceivedMessage = messagesToReceive.await(5, TimeUnit.SECONDS);

        session.disconnect();

        if (failure.get() != null) {
            fail(failure.get().getMessage());
        } else if (!hasReceivedMessage) {
            fail("Test wasn't completed");
        }
    }
}
