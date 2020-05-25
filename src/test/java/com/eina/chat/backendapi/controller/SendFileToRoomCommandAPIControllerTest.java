package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.message.request.SendFileToRoomCommand;
import com.eina.chat.backendapi.protocol.packages.message.response.FileFromRoomResponse;
import com.eina.chat.backendapi.security.AccessLevels;
import com.eina.chat.backendapi.service.EncryptionAPI;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import com.eina.chat.backendapi.service.PersistentDataAPI;
import org.apache.commons.lang3.RandomUtils;
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
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendFileToRoomCommandAPIControllerTest {
    @LocalServerPort
    private int port;

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-api-ws-uri:}")
    private String backEndURI;

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(SendFileToRoomCommandAPIControllerTest.class);

    // Database service
    @Autowired
    private PersistentDataAPI persistentDataAPI;

    // RabbitMQ API
    @Autowired
    private MessageBrokerAPI messageBrokerAPI;

    // Encryption API
    @Autowired
    EncryptionAPI encryptionAPI;

    // Variables
    final private String nameUser1 = "testUser1";
    final private String nameUser2 = "testUser2";

    final private String passUser1 = "test";
    final private String passUser2 = "test";

    final private int sendMessageID = 4;
    final private int sizeOfFileToSend = 100;
    final private byte[] sendFileContent = RandomUtils.nextBytes(sizeOfFileToSend);

    final private String roomName = "testroom";

    @BeforeEach
    public void setupForEach() {
        // Delete users from all databases
        persistentDataAPI.deleteUser(nameUser1);
        persistentDataAPI.deleteUser(nameUser2);

        // Delete groups where are admin
        List<String> groupsWereAdminUser1 = persistentDataAPI.getAllGroupsWhereIsAdmin(nameUser1);
        for (String i : groupsWereAdminUser1) {
            messageBrokerAPI.deleteGroup(i);
        }

        List<String> groupsWereAdminUser2 = persistentDataAPI.getAllGroupsWhereIsAdmin(nameUser1);
        for (String i : groupsWereAdminUser2) {
            messageBrokerAPI.deleteGroup(i);
        }

        // Delete users from broker
        messageBrokerAPI.deleteUser(nameUser1);
        messageBrokerAPI.deleteUser(nameUser2);

        // Create users in database
        persistentDataAPI.createUser(nameUser1, encryptionAPI.asymmetricEncryptString(passUser1), AccessLevels.ROLE_USER);
        persistentDataAPI.createUser(nameUser2, encryptionAPI.asymmetricEncryptString(passUser2), AccessLevels.ROLE_USER);

        // Create users in broker
        messageBrokerAPI.createUser(nameUser1);
        messageBrokerAPI.createUser(nameUser2);

        // Create room
        persistentDataAPI.createGroup(nameUser1, roomName);
        messageBrokerAPI.addUserToGroup(nameUser1, roomName);

        // Add user to room
        persistentDataAPI.addUserToGroup(nameUser2, roomName);
        messageBrokerAPI.addUserToGroup(nameUser2, roomName);
    }

    @AfterEach
    public void cleanForEach() {
        // Delete users from all databases
        persistentDataAPI.deleteUser(nameUser1);
        persistentDataAPI.deleteUser(nameUser2);

        // Delete users from broker
        messageBrokerAPI.deleteUser(nameUser1);
        messageBrokerAPI.deleteUser(nameUser2);

        // Delete created room
        persistentDataAPI.deleteGroup(roomName);
        messageBrokerAPI.deleteGroup(roomName);
    }

    @Test
    public void sendFileFromUserToUserBoothOnline() throws Exception {
        // Handle exceptions in threads
        final AtomicReference<Throwable> failure = new AtomicReference<>();

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

        sessionUser1.subscribe("/user/queue/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/message User 1");

                //failure.set(new Exception("Message arrived to User1"));
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

                if (payload instanceof FileFromRoomResponse &&
                        Arrays.equals(((FileFromRoomResponse) payload).getFile(), sendFileContent) &&
                        ((FileFromRoomResponse) payload).getFromUser().equals(nameUser1) &&
                        ((FileFromRoomResponse) payload).getFromRoom().equals(roomName)) {
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

                else if (errorResponse.getMessageId() == sendMessageID && errorResponse instanceof OperationFailResponse)
                    failure.set(new Exception(((OperationFailResponse) errorResponse).getDescription()));

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

        // Allow subscriptions to set up
        Thread.sleep(1000);

        sessionUser1.send("/app/message", new SendFileToRoomCommand(sendMessageID, roomName, sendFileContent));

        boolean hasReceivedMessage = messagesToReceive.await(5, TimeUnit.SECONDS);

        sessionUser1.disconnect();
        sessionUser2.disconnect();

        if (failure.get() != null) {
            fail(failure.get().getMessage());
        } else if (!hasReceivedMessage) {
            fail("Test wasn't completed");
        }
    }

    @Test
    public void sendFileFromUserToUserOneOffline() throws Exception {
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

        sessionUser1.subscribe("/user/queue/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/message User 1");

                // failureUser1.set(new Exception("Message arrived to User1"));
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

        // Allow subscriptions to set up
        Thread.sleep(1000);

        sessionUser1.send("/app/message", new SendFileToRoomCommand(sendMessageID, roomName, sendFileContent));

        // Check if User1 received ACK
        boolean hasReceivedMessageUser1 = messagesToReceiveUser1.await(5, TimeUnit.SECONDS);

        if (failureUser1.get() != null) {
            fail(failureUser1.get().getMessage());
        } else if (!hasReceivedMessageUser1) {
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

                if (payload instanceof FileFromRoomResponse &&
                        Arrays.equals(((FileFromRoomResponse) payload).getFile(), sendFileContent) &&
                        ((FileFromRoomResponse) payload).getFromUser().equals(nameUser1) &&
                        ((FileFromRoomResponse) payload).getFromRoom().equals(roomName)) {
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

        // Allow subscriptions to set up
        Thread.sleep(1000);

        boolean hasReceivedMessage = messagesToReceiveUser2.await(5, TimeUnit.SECONDS);

        sessionUser2.disconnect();

        if (failureUser2.get() != null) {
            fail(failureUser2.get().getMessage());
        } else if (!hasReceivedMessage) {
            fail("Test wasn't completed User2");
        }
    }
}
