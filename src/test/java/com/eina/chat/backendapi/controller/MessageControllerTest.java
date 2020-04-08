package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.*;
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
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageControllerTest {
    @LocalServerPort
    private int port;

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-api-ws-uri:}")
    private String backEndURI;

    @Test
    public void sendMessageFromUserToUserBoothOnline() throws Exception {
        // Handle exceptions in threads
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        // Variables
        String nameUser1 = "testUser1";
        String nameUser2 = "testUser2";

        String passUser1 = "test";
        String passUser2 = "test";

        WebSocketHttpHeaders headersUser1 = new WebSocketHttpHeaders();
        WebSocketHttpHeaders headersUser2 = new WebSocketHttpHeaders();

        StandardWebSocketClient standardWebSocketClientUser1 = new StandardWebSocketClient();
        StandardWebSocketClient standardWebSocketClientUser2 = new StandardWebSocketClient();

        WebSocketStompClient stompClientUser1 = new WebSocketStompClient(standardWebSocketClientUser1);
        WebSocketStompClient stompClientUser2 = new WebSocketStompClient(standardWebSocketClientUser2);
        stompClientUser1.setMessageConverter(new MappingJackson2MessageConverter());
        stompClientUser2.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders connectHeadersUser1 = new StompHeaders();
        connectHeadersUser1.add("username", nameUser1);
        connectHeadersUser1.add("password", passUser1);

        StompHeaders connectHeadersUser2 = new StompHeaders();
        connectHeadersUser2.add("username", nameUser2);
        connectHeadersUser2.add("password", passUser2);

        // Connect
        StompSession sessionUser1 = stompClientUser1.connect("ws://" + backEndURI + ":{port}/ws", headersUser1, connectHeadersUser1, new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);

        StompSession sessionUser2 = stompClientUser2.connect("ws://" + backEndURI + ":{port}/ws", headersUser2, connectHeadersUser2, new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);

        // Check if connection have failed
        assert (sessionUser1 != null && sessionUser2 != null && sessionUser1.isConnected() && sessionUser2.isConnected());

        // Subscribe to the channels and send message
        // We have to receive 2 messages
        final CountDownLatch messagesToReceive = new CountDownLatch(2);
        final int sendMessageID = 4;
        final String sendMessageContent = "testMessage";

        sessionUser1.subscribe("/user/queue/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageFromUser.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                failure.set(new Exception("Message arrived to User1"));
            }
        });

        sessionUser2.subscribe("/user/queue/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageFromUser.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                if (payload instanceof MessageFromUser &&
                        ((MessageFromUser) payload).getMessage().equals(sendMessageContent) &&
                        ((MessageFromUser) payload).getFrom().equals(nameUser1)) {
                    messagesToReceive.countDown();
                } else {
                    failure.set(new Exception("Message with bad content in User2"));
                }
            }
        });

        sessionUser1.subscribe("/user/queue/error/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ErrorMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                ErrorMessage errorMessage = (ErrorMessage) payload;
                if (errorMessage.getTypeOfMessage() == TypeOfMessage.OPERATION_SUCCEED && errorMessage.getMessageId() == sendMessageID) {
                    messagesToReceive.countDown();
                } else {
                    failure.set(new Exception("Message with bad content in User1 errors"));
                }
            }
        });


        sessionUser2.subscribe("/user/queue/error/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ErrorMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                failure.set(new Exception("Message in User2 errors"));
            }
        });

        sessionUser1.send("/app/message", new SendMessageToUser(sendMessageID, nameUser2, sendMessageContent));

        if (!messagesToReceive.await(10, SECONDS)) {
            fail("Test wasn't completed");
        } else if(failure.get() != null){
            fail(failure.get().getMessage());
        }

        sessionUser1.disconnect();
        sessionUser2.disconnect();
    }
}
