package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.message.request.AddUserToChatRoomCommand;
import com.eina.chat.backendapi.security.AccessLevels;
import com.eina.chat.backendapi.service.GroupsManagementDatabaseAPI;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import com.eina.chat.backendapi.service.MessageHistoryDatabaseAPI;
import com.eina.chat.backendapi.service.UserAccountDatabaseAPI;
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
public class AddUserToRoomCommandAPIControllerTest {
    @LocalServerPort
    private int port;

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-api-ws-uri:}")
    private String backEndURI;

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(AddUserToRoomCommandAPIControllerTest.class);

    // User database service
    @Autowired
    private UserAccountDatabaseAPI userAccountDatabaseAPI;

    @Autowired
    private GroupsManagementDatabaseAPI groupsManagementDatabaseAPI;

    @Autowired
    private MessageHistoryDatabaseAPI messageHistoryDatabaseAPI;

    // RabbitMQ API
    @Autowired
    private MessageBrokerAPI messageBrokerAPI;

    // Variables
    final private String nameAdminUser = "testUser1";
    final private String nameMemberUser = "testUser2";

    final private String passAdminUser = "test";
    final private String passMemberUser = "test";

    final private String roomName = "testroom";

    @BeforeEach
    public void setupForEach() {
        // Delete users from all databases
        userAccountDatabaseAPI.deleteUser(nameAdminUser);
        userAccountDatabaseAPI.deleteUser(nameMemberUser);

        // Delete groups where are admin
        List<String> groupsWereAdminUser1 = groupsManagementDatabaseAPI.getAllGroupsWhereIsAdmin(nameAdminUser);
        for (String i : groupsWereAdminUser1){
            messageHistoryDatabaseAPI.deleteFilesFromGroup(i);
            messageHistoryDatabaseAPI.deleteMessagesFromGroup(i);
            messageBrokerAPI.deleteGroup(i);
        }

        List<String> groupsWereAdminUser2 = groupsManagementDatabaseAPI.getAllGroupsWhereIsAdmin(nameMemberUser);
        for (String i : groupsWereAdminUser2){
            messageHistoryDatabaseAPI.deleteFilesFromGroup(i);
            messageHistoryDatabaseAPI.deleteMessagesFromGroup(i);
            messageBrokerAPI.deleteGroup(i);
        }

        groupsManagementDatabaseAPI.deleteAllGroupsFromAdmin(nameAdminUser);
        groupsManagementDatabaseAPI.deleteAllGroupsFromAdmin(nameMemberUser);

        // Remove group membership
        groupsManagementDatabaseAPI.removeUserFromAllGroups(nameAdminUser);
        groupsManagementDatabaseAPI.removeUserFromAllGroups(nameMemberUser);

        // Delete users from broker
        messageBrokerAPI.deleteUser(nameAdminUser);
        messageBrokerAPI.deleteUser(nameMemberUser);

        // Create users in database
        userAccountDatabaseAPI.createUser(nameAdminUser, passAdminUser, AccessLevels.ROLE_USER);
        userAccountDatabaseAPI.createUser(nameMemberUser, passMemberUser, AccessLevels.ROLE_USER);

        // Create users in broker
        messageBrokerAPI.createUser(nameAdminUser);
        messageBrokerAPI.createUser(nameMemberUser);

        // Delete room
        messageHistoryDatabaseAPI.deleteFilesFromGroup(roomName);
        messageHistoryDatabaseAPI.deleteMessagesFromGroup(roomName);
        messageBrokerAPI.deleteGroup(roomName);

        // Create room
        groupsManagementDatabaseAPI.createGroup(nameAdminUser, roomName);
        messageBrokerAPI.addUserToGroup(nameAdminUser,roomName);
    }

    @BeforeEach
    public void cleanForEach() {
        // Delete users from all databases
        userAccountDatabaseAPI.deleteUser(nameAdminUser);
        userAccountDatabaseAPI.deleteUser(nameMemberUser);

        // Delete users from broker
        messageBrokerAPI.deleteUser(nameAdminUser);
        messageBrokerAPI.deleteUser(nameMemberUser);

        // Delete created room
        groupsManagementDatabaseAPI.deleteGroup(roomName);
        messageBrokerAPI.deleteGroup(roomName);
    }

    @Test
    public void addUserToChatRoomTest() throws Exception {
        // Handle exceptions in threads
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        WebSocketHttpHeaders headersUser1 = new WebSocketHttpHeaders();
        StandardWebSocketClient standardWebSocketClientUser1 = new StandardWebSocketClient();
        WebSocketStompClient stompClientUser1 = new WebSocketStompClient(standardWebSocketClientUser1);
        stompClientUser1.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders connectHeadersUser1 = new StompHeaders();
        connectHeadersUser1.add("username", nameAdminUser);
        connectHeadersUser1.add("password", passAdminUser);

        // Connect
        StompSession sessionUser1 = stompClientUser1.connect("ws://" + backEndURI + ":{port}/ws", headersUser1, connectHeadersUser1, new StompSessionHandlerAdapter() {
        }, this.port).get(2, SECONDS);

        // Check if connection have failed
        assert (sessionUser1 != null && sessionUser1.isConnected());

        // Subscribe to the channels and send message
        // We have to receive 2 messages
        final CountDownLatch messagesToReceive = new CountDownLatch(1);
        final int sendMessageID = 4;

        sessionUser1.subscribe("/user/queue/message", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BasicPackage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info("Message arrived: /user/queue/message User 1");

                failure.set(new Exception("Message arrived to User"));
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

                else if(errorResponse.getMessageId() == sendMessageID && errorResponse instanceof OperationFailResponse)
                    failure.set(new Exception(((OperationFailResponse) errorResponse).getDescription()));

                else
                    failure.set(new Exception("Message with bad content in User1 errors"));
            }
        });

        // Allow subscriptions to set up
        Thread.sleep(1000);

        sessionUser1.send("/app/message", new AddUserToChatRoomCommand(sendMessageID, nameMemberUser, roomName));

        boolean hasReceivedMessage = messagesToReceive.await(5, TimeUnit.SECONDS);

        sessionUser1.disconnect();

        if (failure.get() != null) {
            fail(failure.get().getMessage());
        } else if (!hasReceivedMessage) {
            fail("Test wasn't completed");
        }

        // Check if user have added to room
        assert (groupsManagementDatabaseAPI.checkIfIsGroupMember(nameMemberUser, roomName));
    }
}
