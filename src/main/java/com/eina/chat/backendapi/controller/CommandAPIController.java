package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.message.request.*;
import com.eina.chat.backendapi.protocol.packages.message.response.*;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import com.eina.chat.backendapi.service.*;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
@Timed
public class CommandAPIController {
    /**
     * Simple messaging template
     */
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Encryption API
     */
    @Autowired
    private EncryptionAPI encryptionAPI;

    /**
     * RabbitMQ API
     */
    @Autowired
    private MessageBrokerAPI messageBrokerAPI;

    /**
     * Database API
     */
    @Autowired
    private PersistentDataAPI persistentDataAPI;

    /**
     * Logger
     */
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(CommandAPIController.class);

    @Autowired
    private MeterRegistry meterRegistry;
    /**
     * Max file size in MB
     */
    @Value("${app.max-file-size:}")
    private Integer maxFileSize;

    /**
     * Max message length
     */
    @Value("${app.max-message-length:}")
    private Integer maxMessageLength;

    /**
     * Min and max room length
     */
    @Value("${app.max-room-length:}")
    private Integer maxRoomLength;

    @Value("${app.min-room-length:}")
    private Integer minRoomLength;

    private AtomicInteger rooms = Metrics.gauge("rooms", new AtomicInteger(0));
    private AtomicInteger users_conn = Metrics.gauge("user_conn", new AtomicInteger(0));

    /**
     * Executor user for async tasks
     */
    ExecutorService executorService = Executors.newFixedThreadPool(10);


    /**
     * Command endpoint
     *
     * @param basicPackage command and arguments
     * @param principal    user info
     * @return command response
     */
    @MessageMapping("/message")
    @SendToUser("/queue/error/message")
    public BasicPackage commandAPIMessageHandler(@Payload BasicPackage basicPackage, Principal principal) {
        // Select correct handler for the type of message
        if (basicPackage instanceof AddUserToChatRoomCommand)
            return handlerAddUserToChatRoom(principal.getName(), (AddUserToChatRoomCommand) basicPackage);

        else if (basicPackage instanceof CreateRoomCommand)
            return handlerCreateRoomCommand(principal.getName(), (CreateRoomCommand) basicPackage);

        else if (basicPackage instanceof DeleteAccountCommand)
            return handlerDeleteAccountCommand(principal.getName(), (DeleteAccountCommand) basicPackage);

        else if (basicPackage instanceof DeleteRoomCommand)
            return handlerDeleteRoomCommand(principal.getName(), (DeleteRoomCommand) basicPackage);

        else if (basicPackage instanceof RemoveUserFromChatRoom)
            return handlerDeleteUserFromChatRoom(principal.getName(), (RemoveUserFromChatRoom) basicPackage);

        else if (basicPackage instanceof SendFileToRoomCommand)
            return handlerSendFileToRoomCommand(principal.getName(), (SendFileToRoomCommand) basicPackage);

        else if (basicPackage instanceof SendFileToUserCommand)
            return handlerSendFileToUserCommand(principal.getName(), (SendFileToUserCommand) basicPackage);

        else if (basicPackage instanceof SendMessageToRoomCommand)
            return handlerSendMessageToRoomCommand(principal.getName(), (SendMessageToRoomCommand) basicPackage);

        else if (basicPackage instanceof SendMessageToUserCommand)
            return handlerSendMessageToUserCommand(principal.getName(), (SendMessageToUserCommand) basicPackage);

        else if (basicPackage instanceof GetAdministeredRoomsCommand)
            return handlerGetAdministeredRoomsCommand(principal.getName(), (GetAdministeredRoomsCommand) basicPackage);

        else if (basicPackage instanceof GetJoinedRoomsCommand)
            return handlerGetJoinedRoomsCommand(principal.getName(), (GetJoinedRoomsCommand) basicPackage);

        else if (basicPackage instanceof GetMessageHistoryFromRoomCommand)
            return handlerGetMessageHistoryFromRoomCommand(principal.getName(), (GetMessageHistoryFromRoomCommand) basicPackage);

        else if (basicPackage instanceof GetFileHistoryFromRoomCommand)
            return handlerGetFileHistoryFromRoomCommand(principal.getName(), (GetFileHistoryFromRoomCommand) basicPackage);

        else return new OperationFailResponse(basicPackage.getMessageId(), "Unknown command");
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoomCommand
     *
     * @param username                 user username
     * @param addUserToChatRoomCommand content
     * @return command response
     */
    public BasicPackage handlerAddUserToChatRoom(String username, AddUserToChatRoomCommand addUserToChatRoomCommand) {
        logger.info("Received message from type AddUserToChatRoomCommand from: " + username);
        if (addUserToChatRoomCommand.getRoomName() == null || addUserToChatRoomCommand.getUsername() == null ||
                !persistentDataAPI.checkUserExist(addUserToChatRoomCommand.getUsername()))
            return new OperationFailResponse(addUserToChatRoomCommand.getMessageId(), "Non-existent user or room");

        else if (!persistentDataAPI.checkIfIsGroupAdmin(username, addUserToChatRoomCommand.getRoomName()))
            return new OperationFailResponse(addUserToChatRoomCommand.getMessageId(), "You are not the room admin");

        else {
            persistentDataAPI.addUserToGroup(addUserToChatRoomCommand.getUsername(), addUserToChatRoomCommand.getRoomName()
            );
            messageBrokerAPI.addUserToGroup(addUserToChatRoomCommand.getUsername(), addUserToChatRoomCommand.getRoomName());
            return new OperationSucceedResponse(addUserToChatRoomCommand.getMessageId());
        }
    }

    /**
     * Handle messages received from user with content of type CreateRoomCommand
     *
     * @param username          user username
     * @param createRoomCommand content
     * @return command response
     */
    public BasicPackage handlerCreateRoomCommand(String username, CreateRoomCommand createRoomCommand) {
        logger.info("Received message from type CreateRoomCommand from: " + username);
        if (createRoomCommand.getRoomName() == null || createRoomCommand.getRoomName().length() < minRoomLength ||
                maxRoomLength < createRoomCommand.getRoomName().length() ||
                !createRoomCommand.getRoomName().matches("[a-zA-Z0-9]+"))
            return new OperationFailResponse(createRoomCommand.getMessageId(), "Room name must have between " +
                    minRoomLength.toString() + " and " + maxRoomLength.toString() + " characters (only alphanumeric allowed)");

        else if (persistentDataAPI.checkIfGroupExist(createRoomCommand.getRoomName()))
            return new OperationFailResponse(createRoomCommand.getMessageId(), "The name of the room already exists");

        else {
            rooms.incrementAndGet();
            persistentDataAPI.createGroup(username, createRoomCommand.getRoomName());
            messageBrokerAPI.addUserToGroup(username, createRoomCommand.getRoomName());
            return new OperationSucceedResponse(createRoomCommand.getMessageId());
        }
    }


    /**
     * Handle messages received from user with content of type DeleteAccountCommand
     *
     * @param username             user username
     * @param deleteAccountCommand content
     * @return command response
     */
    public BasicPackage handlerDeleteAccountCommand(String username, DeleteAccountCommand deleteAccountCommand) {
        // TODO: Now the delete is effective once session is closed
        logger.info("Received message from type DeleteAccountCommand from: " + username);

        // Delete all groups where is admin and the associated messages
        List<String> administeredGroups = persistentDataAPI.getAllGroupsWhereIsAdmin(username);
        for (String group : administeredGroups) {
            // TODO Improvement: Send message group members informing the deletion

            // Delete associated broker
            messageBrokerAPI.deleteGroup(group);
        }

        // Delete user account
        persistentDataAPI.deleteUser(username);

        // Delete user from broker
        messageBrokerAPI.deleteUser(username);

        return new OperationSucceedResponse(deleteAccountCommand.getMessageId());
    }

    /**
     * Handle messages received from user with content of type DeleteRoomCommand
     *
     * @param username          user username
     * @param deleteRoomCommand content
     * @return command response
     */
    public BasicPackage handlerDeleteRoomCommand(String username, DeleteRoomCommand deleteRoomCommand) {
        // TODO Notify: May notify all group member of the deletion
        logger.info("Received message from type DeleteRoomCommand from: " + username);
        if (deleteRoomCommand.getRoomName() == null ||
                !persistentDataAPI.checkIfIsGroupAdmin(username, deleteRoomCommand.getRoomName()))
            return new OperationFailResponse(deleteRoomCommand.getMessageId(), "You are not the room admin");

        else {
            // Delete group
            persistentDataAPI.deleteGroup(deleteRoomCommand.getRoomName());

            // Delete group from broker
            messageBrokerAPI.deleteGroup(deleteRoomCommand.getRoomName());
            rooms.decrementAndGet();
            return new OperationSucceedResponse(deleteRoomCommand.getMessageId());
        }
    }

    /**
     * Handle messages received from user with content of type DeleteUserFromChatRoom
     *
     * @param username               user username
     * @param removeUserFromChatRoom content
     * @return command response
     */
    public BasicPackage handlerDeleteUserFromChatRoom(String username, RemoveUserFromChatRoom removeUserFromChatRoom) {
        // TODO Improvement: May notify of the deletion
        logger.info("Received message from type DeleteUserFromChatRoom from: " + username);
        if (removeUserFromChatRoom.getRoomName() == null || removeUserFromChatRoom.getUsername() == null)
            return new OperationFailResponse(removeUserFromChatRoom.getMessageId(), "Non-existent user or room");

        else if (!persistentDataAPI.checkIfIsGroupAdmin(username, removeUserFromChatRoom.getRoomName()))
            return new OperationFailResponse(removeUserFromChatRoom.getMessageId(), "You are not the room admin");

        else if (!persistentDataAPI.checkIfIsGroupMember(removeUserFromChatRoom.getUsername(), removeUserFromChatRoom.getRoomName()))
            return new OperationFailResponse(removeUserFromChatRoom.getMessageId(), "Non-existent user in the room");

        else if (persistentDataAPI.checkIfIsGroupAdmin(removeUserFromChatRoom.getUsername(), removeUserFromChatRoom.getRoomName()))
            return new OperationFailResponse(removeUserFromChatRoom.getMessageId(), "You can not remove yourself from the room");

        else {
            persistentDataAPI.removeUserFromGroup(removeUserFromChatRoom.getUsername(), removeUserFromChatRoom.getRoomName()
            );
            messageBrokerAPI.removeUserFromGroup(removeUserFromChatRoom.getUsername(), removeUserFromChatRoom.getRoomName());
            return new OperationSucceedResponse(removeUserFromChatRoom.getMessageId());
        }
    }

    /**
     * Handle messages received from user with content of type SendFileToRoomCommand
     *
     * @param username              user username
     * @param sendFileToRoomCommand content
     * @return command response
     */
    public BasicPackage handlerSendFileToRoomCommand(String username, SendFileToRoomCommand sendFileToRoomCommand) {
        logger.info("Received message from type SendFileToRoomCommand from: " + username);
        if (sendFileToRoomCommand.getRoomName() == null)
            return new OperationFailResponse(sendFileToRoomCommand.getMessageId(), "Non-existent room");

        else if (!persistentDataAPI.checkIfIsGroupMember(username, sendFileToRoomCommand.getRoomName()))
            return new OperationFailResponse(sendFileToRoomCommand.getMessageId(), "You are not member of the room");

        else if (sendFileToRoomCommand.getFile() == null || sendFileToRoomCommand.getFile().length == 0 ||
                sendFileToRoomCommand.getFile().length > maxFileSize * 1000000)
            return new OperationFailResponse(sendFileToRoomCommand.getMessageId(), "File must have between 1 and "
                    + (maxFileSize * 1000000) + " bytes");

        else {
            messageBrokerAPI.sendFileToGroup(username, sendFileToRoomCommand.getRoomName(),
                    encryptionAPI.symmetricEncryptFile(sendFileToRoomCommand.getFile()));
            persistentDataAPI.saveFileToGroup(username, sendFileToRoomCommand.getRoomName(),
                    encryptionAPI.symmetricEncryptFile(sendFileToRoomCommand.getFile()));
            Metrics.counter("message_sent", "type", "fileG", "from", username,
                    "to", sendFileToRoomCommand.getRoomName()).increment();
            return new OperationSucceedResponse(sendFileToRoomCommand.getMessageId());
        }
    }

    /**
     * Handle messages received from user with content of type SendFileToUserCommand
     *
     * @param username              user username
     * @param sendFileToUserCommand content
     * @return command response
     */
    public BasicPackage handlerSendFileToUserCommand(String username, SendFileToUserCommand sendFileToUserCommand) {
        logger.info("Received message from type SendFileToUserCommand from: " + username);
        if (sendFileToUserCommand.getUsername() == null || !persistentDataAPI.checkUserExist(sendFileToUserCommand.getUsername()))
            return new OperationFailResponse(sendFileToUserCommand.getMessageId(), "Non-existent user");

        else if (sendFileToUserCommand.getFile() == null || sendFileToUserCommand.getFile().length == 0 ||
                sendFileToUserCommand.getFile().length > maxFileSize * 1000000)
            return new OperationFailResponse(sendFileToUserCommand.getMessageId(), "File must have between 1 and "
                    + (maxFileSize * 1000000) + " bytes");

        else {
            messageBrokerAPI.sendFileToUser(username, sendFileToUserCommand.getUsername(),
                    encryptionAPI.symmetricEncryptFile(sendFileToUserCommand.getFile()));
            Metrics.counter("message_sent", "type", "fileU", "from", username,
                    "to", sendFileToUserCommand.getUsername()).increment();
            return new OperationSucceedResponse(sendFileToUserCommand.getMessageId());
        }
    }

    /**
     * Handle messages received from user with content of type SendMessageToRoomCommand
     *
     * @param username                 user username
     * @param sendMessageToRoomCommand content
     * @return command response
     */
    public BasicPackage handlerSendMessageToRoomCommand(String username, SendMessageToRoomCommand sendMessageToRoomCommand) {
        logger.info("Received message from type SendMessageToRoomCommand from: " + username);
        if (sendMessageToRoomCommand.getRoomName() == null)
            return new OperationFailResponse(sendMessageToRoomCommand.getMessageId(), "Non-existent room");

        else if (!persistentDataAPI.checkIfIsGroupMember(username, sendMessageToRoomCommand.getRoomName()))
            return new OperationFailResponse(sendMessageToRoomCommand.getMessageId(), "You are not member of the room");

        else if (sendMessageToRoomCommand.getMessage() == null || sendMessageToRoomCommand.getMessage().isBlank() ||
                sendMessageToRoomCommand.getMessage().length() > maxMessageLength)
            return new OperationFailResponse(sendMessageToRoomCommand.getMessageId(), "Message must have between 1 and "
                    + maxMessageLength + " characters");

        else {
            messageBrokerAPI.sendMessageToGroup(username, sendMessageToRoomCommand.getRoomName(),
                    encryptionAPI.symmetricEncryptString(sendMessageToRoomCommand.getMessage()));
            persistentDataAPI.saveMessageFromGroup(username, sendMessageToRoomCommand.getRoomName(),
                    encryptionAPI.symmetricEncryptString(sendMessageToRoomCommand.getMessage()));
            Metrics.counter("message_sent", "type", "msgG", "from", username,
                    "to", sendMessageToRoomCommand.getRoomName()).increment();
            return new OperationSucceedResponse(sendMessageToRoomCommand.getMessageId());
        }
    }

    /**
     * Handle messages received from user with content of type SendMessageToUserCommand
     *
     * @param username                 user username
     * @param sendMessageToUserCommand content
     * @return command response
     */
    public BasicPackage handlerSendMessageToUserCommand(String username, SendMessageToUserCommand sendMessageToUserCommand) {
        logger.info("Received message from type SendMessageToUserCommand from: " + username);
        if (sendMessageToUserCommand.getUsername() == null || !persistentDataAPI.checkUserExist(sendMessageToUserCommand.getUsername()))
            return new OperationFailResponse(sendMessageToUserCommand.getMessageId(), "Non-existent user");

        else if (sendMessageToUserCommand.getMessage() == null || sendMessageToUserCommand.getMessage().isBlank() ||
                sendMessageToUserCommand.getMessage().length() > maxMessageLength)
            return new OperationFailResponse(sendMessageToUserCommand.getMessageId(), "Message must have between 1 and "
                    + maxMessageLength + " characters");

        else {
            messageBrokerAPI.sendMessageToUser(username, sendMessageToUserCommand.getUsername(),
                    encryptionAPI.symmetricEncryptString(sendMessageToUserCommand.getMessage()));
            Metrics.counter("message_sent", "type", "msgU", "from", username,
                    "to", sendMessageToUserCommand.getUsername()).increment();
            return new OperationSucceedResponse(sendMessageToUserCommand.getMessageId());
        }
    }

    /**
     * Handle messages received from user with content of type GetAdministeredRoomsCommand
     *
     * @param username                    user username
     * @param getAdministeredRoomsCommand content
     * @return command response
     */
    public BasicPackage handlerGetAdministeredRoomsCommand(String username, GetAdministeredRoomsCommand getAdministeredRoomsCommand) {
        logger.info("Received message from type GetAdministeredRoomsCommand from: " + username);
        executorService.submit(() -> simpMessagingTemplate.convertAndSendToUser(username, "/queue/message",
                new AdministeredRoomsResponse(getAdministeredRoomsCommand.getMessageId(),
                        persistentDataAPI.getAllGroupsWhereIsAdmin(username))));
        return new OperationSucceedResponse(getAdministeredRoomsCommand.getMessageId());
    }

    /**
     * Handle messages received from user with content of type GetFileHistoryFromRoomCommand
     *
     * @param username                      user username
     * @param getFileHistoryFromRoomCommand content
     * @return command response
     */
    public BasicPackage handlerGetFileHistoryFromRoomCommand(String username, GetFileHistoryFromRoomCommand getFileHistoryFromRoomCommand) {
        logger.info("Received message from type GetFileHistoryFromRoomCommand from: " + username);
        if (persistentDataAPI.checkIfIsGroupMember(username, getFileHistoryFromRoomCommand.getRoomName())) {
            executorService.submit(() -> {
                List<Pair<String, byte[]>> usersFiles = persistentDataAPI.getOrderedFilesFromGroup(getFileHistoryFromRoomCommand.getRoomName());
                List<String> users = usersFiles.stream().map(Pair::getLeft).collect(Collectors.toList());
                List<byte[]> files = usersFiles.stream().map(file -> encryptionAPI.symmetricDecryptFile(file.getRight()))
                        .collect(Collectors.toList());
                simpMessagingTemplate.convertAndSendToUser(username, "/queue/message",
                        new FileHistoryFromRoomResponse(getFileHistoryFromRoomCommand.getMessageId(),
                                getFileHistoryFromRoomCommand.getRoomName(), users, files));
            });
            return new OperationSucceedResponse(getFileHistoryFromRoomCommand.getMessageId());
        } else {
            return new OperationFailResponse(getFileHistoryFromRoomCommand.getMessageId(), "You are not group member");
        }
    }

    /**
     * Handle messages received from user with content of type GetJoinedRoomsCommand
     *
     * @param username              user username
     * @param getJoinedRoomsCommand content
     * @return command response
     */
    public BasicPackage handlerGetJoinedRoomsCommand(String username, GetJoinedRoomsCommand getJoinedRoomsCommand) {
        logger.info("Received message from type GetJoinedRoomsCommand from: " + username);
        executorService.submit(() -> simpMessagingTemplate.convertAndSendToUser(username, "/queue/message",
                new JoinedRoomsResponse(getJoinedRoomsCommand.getMessageId(),
                        persistentDataAPI.getAllGroupsWhereIsMember(username))));
        return new OperationSucceedResponse(getJoinedRoomsCommand.getMessageId());
    }

    /**
     * Handle messages received from user with content of type GetMessageHistoryFromRoomCommand
     *
     * @param username                         user username
     * @param getMessageHistoryFromRoomCommand content
     * @return command response
     */
    public BasicPackage handlerGetMessageHistoryFromRoomCommand(String username, GetMessageHistoryFromRoomCommand getMessageHistoryFromRoomCommand) {
        logger.info("Received message from type GetMessageHistoryFromRoomCommand from: " + username);
        if (persistentDataAPI.checkIfIsGroupMember(username, getMessageHistoryFromRoomCommand.getRoomName())) {
            executorService.submit(() -> {
                List<Pair<String, String>> usersFiles = persistentDataAPI.getOrderedMessagesFromGroup(getMessageHistoryFromRoomCommand.getRoomName());
                List<String> users = usersFiles.stream().map(Pair::getLeft).collect(Collectors.toList());
                List<String> messages = usersFiles.stream().map(message -> encryptionAPI.symmetricDecryptString(message.getRight()))
                        .collect(Collectors.toList());
                simpMessagingTemplate.convertAndSendToUser(username, "/queue/message",
                        new MessageHistoryFromRoomResponse(getMessageHistoryFromRoomCommand.getMessageId(),
                                getMessageHistoryFromRoomCommand.getRoomName(), users, messages));
            });
            return new OperationSucceedResponse(getMessageHistoryFromRoomCommand.getMessageId());
        } else {
            return new OperationFailResponse(getMessageHistoryFromRoomCommand.getMessageId(), "You are not group member");
        }
    }

    /**
     * Notify when user is subscribed to any channel
     */
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        simpDestination = simpDestination != null ? simpDestination : "";

        String username = event.getUser() != null ? event.getUser().getName() : "";

        // Log
        logger.info("Session Subscribe Event on endpoint: " + simpDestination + " by user: " + username);

        if (simpDestination.equals("/user/queue/message") && !username.isBlank()) {
            users_conn.incrementAndGet();
            messageBrokerAPI.addUserReceiverMessagesCallback(username, new ReceiveHandler() {
                @Override
                public void onUserMessageArrive(String fromUsername, String message) {
                    logger.info("User message arrive callback with message from: " + fromUsername);
                    simpMessagingTemplate.convertAndSendToUser(username,
                            "/queue/message",
                            new MessageFromUserResponse(fromUsername, encryptionAPI.symmetricDecryptString(message)));
                }

                @Override
                public void onGroupMessageArrive(String fromUsername, String group, String message) {
                    logger.info("Group message arrive callback with message from: " + fromUsername + " and group:" + group);
                    simpMessagingTemplate.convertAndSendToUser(username,
                            "/queue/message",
                            new MessageFromRoomResponse(fromUsername, group, encryptionAPI.symmetricDecryptString(message)));
                }

                @Override
                public void onUserFileArrive(String fromUsername, byte[] file) {
                    logger.info("User file arrive callback with message from: " + fromUsername);
                    simpMessagingTemplate.convertAndSendToUser(username,
                            "/queue/message",
                            new FileFromUserResponse(fromUsername, encryptionAPI.symmetricDecryptFile(file)));
                }

                @Override
                public void onGroupFileArrive(String fromUsername, String group, byte[] file) {
                    logger.info("Group file arrive callback with message from: " + fromUsername + " and group:" + group);
                    simpMessagingTemplate.convertAndSendToUser(username,
                            "/queue/message",
                            new FileFromRoomResponse(fromUsername, group, encryptionAPI.symmetricDecryptFile(file)));
                }

                @Override
                public void onNotificationArrive(String content) {
                    // TODO Improvement: Implement
                }
            });
        }
    }

    /**
     * Notify when user is unsubscribed to any channel
     */
    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        simpDestination = simpDestination != null ? simpDestination : "";

        String username = event.getUser() != null ? event.getUser().getName() : "";

        // Log
        logger.info("Session Unsubscribe Event on endpoint: " + simpDestination + " by user: " + username);

        // Unsubscription
        if (simpDestination.equals("/user/queue/message") && !username.isBlank()) {
            users_conn.decrementAndGet();
            messageBrokerAPI.deleteUserReceiverMessagesCallback(username);
        }
    }

    /**
     * Notify when user is disconnected
     */
    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        String username = event.getUser() != null ? event.getUser().getName() : "";

        // Log
        logger.info("Session Disconnect Event by user: " + username);

        // Unsubscription from RabbitMQ
        if (!username.isBlank()) {
            users_conn.decrementAndGet();
            messageBrokerAPI.deleteUserReceiverMessagesCallback(username);
        }
    }
}
