package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.errors.WSResponseStatus;
import com.eina.chat.backendapi.model.User;
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
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        SignUpEndpointStompSessionHandler handler = new SignUpEndpointStompSessionHandler(failure, new User("user", "password"), messagesToReceive);

        StompHeaders connectHeadersUser1 = new StompHeaders();
        connectHeadersUser1.add("username", "nameUser1");
        connectHeadersUser1.add("password", "passUser1");

        // TODO: Check why fail without credentials
        ListenableFuture<StompSession> session = this.stompClient.connect("ws://" + backEndURI + ":{port}/ws", this.headers,connectHeadersUser1, handler, this.port);

        if (messagesToReceive.await(10, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            }
            ArrayList<WSResponseStatus> messagesCaptured = handler.getMessagesCaptured();
            assertEquals(1, messagesCaptured.size());
            assertEquals("User created", messagesCaptured.get(0).getStatus());
        } else {
            fail("Original URL not received");
        }

        session.completable().get().disconnect();
    }

    /**
     * Test signing up a user that exist yet
     */
    @Test
    public void repeatedUser() throws Exception {
        // TODO: Implement method
    }

    private static class SignUpEndpointStompSessionHandler extends StompSessionHandlerAdapter {

        private final AtomicReference<Throwable> failure;
        private final ErrorFrameHandler errorFrameHandler;
        private final User user;
        private final CountDownLatch latch;

        SignUpEndpointStompSessionHandler(AtomicReference<Throwable> failure,
                                          User user, CountDownLatch latch) {
            this.failure = failure;
            this.user = user;
            this.latch = latch;
            errorFrameHandler = new ErrorFrameHandler(latch);
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            // Subscribe to errors
            session.subscribe("/user/queue/error/sign-up", errorFrameHandler);

            session.send("/app/sign-up", user);
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Exception(headers.toString()));
        }

        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
            this.failure.set(ex);
            while (latch.getCount() > 0)
                latch.countDown();
        }

        @Override
        public void handleTransportError(StompSession session, Throwable ex) {
            this.failure.set(ex);
            while (latch.getCount() > 0)
                latch.countDown();
        }

        ArrayList<WSResponseStatus> getMessagesCaptured() {
            return errorFrameHandler.getMessagesCaptured();
        }

    }

    /**
     * Error handler
     */
    private static class ErrorFrameHandler implements StompFrameHandler {
        private CountDownLatch latch;
        private ArrayList<WSResponseStatus> messagesCaptured = new ArrayList<>();

        ErrorFrameHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return WSResponseStatus.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            messagesCaptured.add((WSResponseStatus) payload);
            latch.countDown();
        }

        ArrayList<WSResponseStatus> getMessagesCaptured() {
            return messagesCaptured;
        }
    }
}
