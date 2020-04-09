package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.model.User;
import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.message.request.SendMessageToUserCommand;
import com.eina.chat.backendapi.protocol.packages.message.response.MessageFromUserResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.OperationSucceedResponse;
import com.eina.chat.backendapi.service.UserAccountDatabaseAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MessageControllerTest {
    @LocalServerPort
    private int port;

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-api-ws-uri:}")
    private String backEndURI;

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(MessageControllerTest.class);

    // User database service
    @Autowired
    private UserAccountDatabaseAPI userAccountDatabaseAPI;

    // Variables
    final private String nameUser1 = "testUser1";
    final private String nameUser2 = "testUser2";

    final private String passUser1 = "test";
    final private String passUser2 = "test";


    @BeforeEach
    public void setupForEach() {
        if (userAccountDatabaseAPI.checkUserExist(nameUser1)) {
            userAccountDatabaseAPI.deleteUser(nameUser1);
        }
        if (userAccountDatabaseAPI.checkUserExist(nameUser2)) {
            userAccountDatabaseAPI.deleteUser(nameUser2);
        }

        userAccountDatabaseAPI.createUser(new User(nameUser1, passUser1));
        userAccountDatabaseAPI.createUser(new User(nameUser2, passUser2));
    }

    @AfterEach
    public void deleteForEach() {
        if (userAccountDatabaseAPI.checkUserExist(nameUser1)) {
            userAccountDatabaseAPI.deleteUser(nameUser1);
        }
        if (userAccountDatabaseAPI.checkUserExist(nameUser2)) {
            userAccountDatabaseAPI.deleteUser(nameUser2);
        }
    }


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
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/message User 1");

                failure.set(new Exception("Message arrived to User1"));
            }
        });

        sessionUser2.subscribe("/user/queue/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/message User 2");

                if (payload instanceof MessageFromUserResponse &&
                        ((MessageFromUserResponse) payload).getMessage().equals(sendMessageContent) &&
                        ((MessageFromUserResponse) payload).getFrom().equals(nameUser1)) {
                    messagesToReceive.countDown();
                } else {
                    failure.set(new Exception("Message with bad content in User2"));
                }
            }
        });

        sessionUser1.subscribe("/user/queue/error/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/error/message User 1");

                BasicPackage errorResponse = (BasicPackage) payload;
                if (errorResponse.getMessageId() == sendMessageID && errorResponse instanceof OperationSucceedResponse)
                    messagesToReceive.countDown();
                else
                    failure.set(new Exception("Message with bad content in User1 errors"));
            }
        });


        sessionUser2.subscribe("/user/queue/error/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/error/message User 2");

                failure.set(new Exception("Message in User2 errors"));
            }
        });

        sessionUser1.send("/app/message", new SendMessageToUserCommand(sendMessageID, nameUser2, sendMessageContent));

        boolean hasReceivedMessage = messagesToReceive.await(5, TimeUnit.SECONDS);

        sessionUser1.disconnect();
        sessionUser2.disconnect();

        if (failure.get() != null) {
            fail(failure.get().getMessage());
        } else if (hasReceivedMessage) {
            fail("Test wasn't completed");
        }
    }

    @Test
    public void sendMessageFromUserToUserOneOffline() throws Exception {
        // Handle exceptions in threads
        final AtomicReference<Throwable> failureUser1 = new AtomicReference<>();
        final AtomicReference<Throwable> failureUser2 = new AtomicReference<>();

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

        // Connect only User1 yet
        StompSession sessionUser1 = stompClientUser1.connect("ws://" + backEndURI + ":{port}/ws", headersUser1, connectHeadersUser1, new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);

        // Check if connection have failed
        assert (sessionUser1 != null && sessionUser1.isConnected());

        // Subscribe to the channels and send message
        // We have to receive 2 messages
        final CountDownLatch messagesToReceiveUser1 = new CountDownLatch(1);
        final CountDownLatch messagesToReceiveUser2 = new CountDownLatch(1);
        final int sendMessageID = 4;
        final String sendMessageContent = "testMessage";

        sessionUser1.subscribe("/user/queue/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/message User 1");

                failureUser1.set(new Exception("Message arrived to User1"));
            }
        });

        sessionUser1.subscribe("/user/queue/error/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/error/message User 1");

                BasicPackage errorResponse = (BasicPackage) payload;
                if (errorResponse.getMessageId() == sendMessageID && errorResponse instanceof OperationSucceedResponse)
                    messagesToReceiveUser1.countDown();
                else
                    failureUser1.set(new Exception("Message with bad content in User1 errors"));
            }
        });


        sessionUser1.send("/app/message", new SendMessageToUserCommand(sendMessageID, nameUser2, sendMessageContent));

        // Check if User1 received ACK
        boolean hasReceivedMessageUser1 = messagesToReceiveUser1.await(5, TimeUnit.SECONDS);

        if (failureUser1.get() != null) {
            fail(failureUser1.get().getMessage());
        } else if (hasReceivedMessageUser1) {
            fail("Test wasn't completed User1");
        }

        // Connect only User1 yet
        sessionUser1.disconnect();

        // Now connect to User2
        StompSession sessionUser2 = stompClientUser2.connect("ws://" + backEndURI + ":{port}/ws", headersUser2, connectHeadersUser2, new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);

        // Check if connection have failed
        assert (sessionUser2 != null && sessionUser2.isConnected());

        sessionUser2.subscribe("/user/queue/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/message User 2");

                if (payload instanceof MessageFromUserResponse &&
                        ((MessageFromUserResponse) payload).getMessage().equals(sendMessageContent) &&
                        ((MessageFromUserResponse) payload).getFrom().equals(nameUser1)) {
                    messagesToReceiveUser2.countDown();
                } else {
                    failureUser2.set(new Exception("Message with bad content in User2"));
                }
            }
        });

        sessionUser2.subscribe("/user/queue/error/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/error/message User 2");

                failureUser2.set(new Exception("Message in User2 errors"));
            }
        });

        sessionUser2.disconnect();

        boolean hasReceivedMessage = messagesToReceiveUser2.await(5, TimeUnit.SECONDS);

        if (failureUser2.get() != null) {
            fail(failureUser2.get().getMessage());
        } else if (hasReceivedMessage) {
            fail("Test wasn't completed User2");
        }
    }
}
