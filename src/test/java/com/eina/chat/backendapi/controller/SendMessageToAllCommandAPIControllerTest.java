package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.admin.request.SendMessageToAllCommand;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.MessageFromUserResponse;
import com.eina.chat.backendapi.security.AccessLevels;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import com.eina.chat.backendapi.service.PersistentDataAPI;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendMessageToAllCommandAPIControllerTest {

    @LocalServerPort
    private int port;

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-api-ws-uri:}")
    private String backEndURI;

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(com.eina.chat.backendapi.controller.SendMessageFromUserToUserCommandAPIControllerTest.class);

    // Database service
    @Autowired
    private PersistentDataAPI persistentDataAPI;

    // RabbitMQ API
    @Autowired
    private MessageBrokerAPI messageBrokerAPI;

    // Variables
    final private String nameAdmin = "admin";
    final private String nameUser1 = "testUser1";
    final private String nameUser2 = "testUser2";

    final private String passAdmin = "admin";
    final private String passUser = "test";

    final private int sendMessageID = 4;
    final private String sendMessageContent = "AppUpdate";


    @BeforeEach
    public void setupForEach() {
        // Delete users from all databases
        persistentDataAPI.deleteUser(nameAdmin);
        persistentDataAPI.deleteUser(nameUser1);
        persistentDataAPI.deleteUser(nameUser2);

        // Delete groups where are admin
        List<String> groupsWereAdminUser1 = persistentDataAPI.getAllGroupsWhereIsAdmin(nameUser1);
        for (String i : groupsWereAdminUser1){
            messageBrokerAPI.deleteGroup(i);
        }

        List<String> groupsWereAdminUser2 = persistentDataAPI.getAllGroupsWhereIsAdmin(nameUser1);
        for (String i : groupsWereAdminUser2){
            messageBrokerAPI.deleteGroup(i);
        }

        // Delete users from broker
        messageBrokerAPI.deleteUser(nameUser1);
        messageBrokerAPI.deleteUser(nameUser2);

        // Create users in database
        persistentDataAPI.createUser(nameUser1, passUser, AccessLevels.ROLE_USER);
        persistentDataAPI.createUser(nameUser2, passUser, AccessLevels.ROLE_USER);
        persistentDataAPI.createUser(nameAdmin, passAdmin, AccessLevels.ROLE_ADMIN);

        // Create users in broker
        messageBrokerAPI.createUser(nameUser1);
        messageBrokerAPI.createUser(nameUser2);
    }

    @BeforeEach
    public void cleanForEach() {
        // Delete users from all databases
        persistentDataAPI.deleteUser(nameUser1);
        persistentDataAPI.deleteUser(nameUser2);
        persistentDataAPI.deleteUser(nameAdmin);

        // Delete users from broker
        messageBrokerAPI.deleteUser(nameUser1);
        messageBrokerAPI.deleteUser(nameUser2);
    }


    @Test
    public void sendMessageFromUserToAllOneOnlineOneOffline() throws Exception {
        // Handle exceptions in threads
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        WebSocketHttpHeaders headersUser1 = new WebSocketHttpHeaders();
        WebSocketHttpHeaders headersUser2 = new WebSocketHttpHeaders();
        WebSocketHttpHeaders headersAdmin = new WebSocketHttpHeaders();

        StandardWebSocketClient standardWebSocketClientUser1 = new StandardWebSocketClient();
        StandardWebSocketClient standardWebSocketClientUser2 = new StandardWebSocketClient();
        StandardWebSocketClient standardWebSocketClientAdmin = new StandardWebSocketClient();

        WebSocketStompClient stompClientUser1 = new WebSocketStompClient(standardWebSocketClientUser1);
        WebSocketStompClient stompClientUser2 = new WebSocketStompClient(standardWebSocketClientUser2);
        WebSocketStompClient stompClientAdmin = new WebSocketStompClient(standardWebSocketClientAdmin);
        stompClientUser1.setMessageConverter(new MappingJackson2MessageConverter());
        stompClientUser2.setMessageConverter(new MappingJackson2MessageConverter());
        stompClientAdmin.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders connectHeadersUser1 = new StompHeaders();
        connectHeadersUser1.add("username", nameUser1);
        connectHeadersUser1.add("password", passUser);

        StompHeaders connectHeadersUser2 = new StompHeaders();
        connectHeadersUser2.add("username", nameUser2);
        connectHeadersUser2.add("password", passUser);

        StompHeaders connectHeadersAdmin = new StompHeaders();
        connectHeadersAdmin.add("username", nameAdmin);
        connectHeadersAdmin.add("password", passAdmin);

        // Connect only user 1 and admin
        StompSession sessionUser1 = stompClientUser1.connect("ws://" + backEndURI + ":{port}/ws", headersUser1, connectHeadersUser1, new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);
        StompSession sessionAdmin = stompClientAdmin.connect("ws://" + backEndURI + ":{port}/ws", headersAdmin, connectHeadersAdmin, new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);

        // Check if connection have failed
        assert (sessionUser1 != null && sessionAdmin != null &&
                sessionUser1.isConnected() && sessionAdmin.isConnected());

        // Subscribe to the channels and send message
        // We have to receive 2 messages
        final CountDownLatch messagesToReceiveUser1 = new CountDownLatch(1);
        final CountDownLatch messagesToReceiveUser2 = new CountDownLatch(1);
        final CountDownLatch messagesToReceiveAdmin = new CountDownLatch(1);

        sessionAdmin.subscribe("/user/queue/error/admin", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/error/admin");

                BasicPackage errorResponse = (BasicPackage) payload;
                if (errorResponse.getMessageId() == sendMessageID && errorResponse instanceof OperationSucceedResponse)
                    messagesToReceiveAdmin.countDown();

                else if(errorResponse.getMessageId() == sendMessageID && errorResponse instanceof OperationFailResponse)
                    failure.set(new Exception(((OperationFailResponse) errorResponse).getDescription()));

                else
                    failure.set(new Exception("Message with bad content in Admin errors"));
            }
        });

        sessionUser1.subscribe("/user/queue/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/message User 1");

                if (payload instanceof MessageFromUserResponse &&
                        ((MessageFromUserResponse) payload).getMessage().equals(sendMessageContent) &&
                        ((MessageFromUserResponse) payload).getFrom().equals(nameAdmin)) {
                    messagesToReceiveUser1.countDown();
                } else {
                    failure.set(new Exception("Message with bad content in User1"));
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

                failure.set(new Exception("Message in User2 errors"));
            }
        });

        // Allow subscriptions to set up
        Thread.sleep(1000);

        sessionAdmin.send("/app/admin", new SendMessageToAllCommand(sendMessageID, sendMessageContent));

        boolean hasReceivedMessage = messagesToReceiveUser1.await(5, TimeUnit.SECONDS);

        if (failure.get() != null) {
            fail(failure.get().getMessage());
        } else if (!hasReceivedMessage) {
            fail("User1 hasn't received message from admin");
        }

        // Disconnect user1 and connect user2
        sessionUser1.disconnect();

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
                        ((MessageFromUserResponse) payload).getFrom().equals(nameAdmin)) {
                    messagesToReceiveUser2.countDown();
                } else {
                    failure.set(new Exception("Message with bad content in User2"));
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

                failure.set(new Exception("Message in User2 errors"));
            }
        });

        // Allow subscriptions to set up
        Thread.sleep(1000);

        hasReceivedMessage = messagesToReceiveUser2.await(5, TimeUnit.SECONDS) &&
                            messagesToReceiveAdmin.await(5, TimeUnit.SECONDS);

        sessionUser2.disconnect();
        sessionAdmin.disconnect();

        if (failure.get() != null) {
            fail(failure.get().getMessage());
        } else if (!hasReceivedMessage) {
            fail("User2 hasn't received message from admin");
        }
    }
}
