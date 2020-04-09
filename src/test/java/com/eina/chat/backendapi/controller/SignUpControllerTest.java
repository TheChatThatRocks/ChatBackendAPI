package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.signup.request.AddAccountCommand;
import com.eina.chat.backendapi.protocol.packages.signup.response.SignUpSuccessResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
        AddAccountCommand sendCommandPackage = new AddAccountCommand(messageId, "user", "password");

        StompSession session = this.stompClient.connect("ws://" + backEndURI + ":{port}/ws", this.headers, new StompSessionHandlerAdapter() {
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
                if(errorResponse.getMessageId() == messageId && errorResponse instanceof SignUpSuccessResponse)
                    messagesToReceive.countDown();
                else {
                    failure.set(new Exception("Unexpected message received or sign-up fail"));
                }
            }
        });

        session.send("/app/sign-up", sendCommandPackage);

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
