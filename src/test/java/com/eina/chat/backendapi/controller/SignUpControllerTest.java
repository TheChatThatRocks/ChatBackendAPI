package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.errors.WSResponseStatus;
import com.eina.chat.backendapi.model.User;
import com.eina.chat.backendapi.protocol.packages.AddAccount;
import com.eina.chat.backendapi.protocol.packages.ErrorResponse;
import com.eina.chat.backendapi.protocol.packages.TypeOfMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SignUpControllerTest {
    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-api-ws-uri:}")
    private String backEndURI;

    @BeforeAll
    public void setup() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();

        this.stompClient = new WebSocketStompClient(standardWebSocketClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    /**
     * Test signing up a new user
     */
    @Test
    public void newUser() throws Exception {
        // Failure variable
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        // Message id
        final int messageId = 1;

        // New user
        AddAccount addAccount = new AddAccount(messageId, "user", "password");

        // Session creation
        StompHeaders connectHeadersUser1 = new StompHeaders();
        connectHeadersUser1.add("username", "nameUser1");
        connectHeadersUser1.add("password", "passUser1");

        // TODO: Check why fail without credentials
//        StompSession session = this.stompClient.connect("ws://" + backEndURI + ":{port}/ws", this.headers, connectHeadersUser1, new StompSessionHandlerAdapter() {
//        }, this.port).get(2, SECONDS);

        StompSession session = this.stompClient.connect("ws://" + backEndURI + ":{port}/ws", this.headers, new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);

        // Check if connection have failed
        assert (session != null && session.isConnected());

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        // Subscribe
        session.subscribe("/user/queue/error/sign-up", new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ErrorResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                ErrorResponse errorResponse = (ErrorResponse) payload;
                if (errorResponse.getMessageId() == messageId && errorResponse.getTypeOfMessage() == TypeOfMessage.SIGN_UP_SUCCESS) {
                    messagesToReceive.countDown();
                } else {
                    failure.set(new Exception("Unexpected message received or sign-up fail"));
                }
            }
        });

        session.send("/app/sign-up", addAccount);

        if (!messagesToReceive.await(10, TimeUnit.SECONDS)) {
            fail("Test wasn't completed");
        } else if (failure.get() != null) {
            fail(failure.get().getMessage());
        }

        session.disconnect();
    }

    /**
     * Test signing up a user that exist yet
     */
    @Test
    public void duplicatedUser() {
        // TODO: Implement method
    }
}
