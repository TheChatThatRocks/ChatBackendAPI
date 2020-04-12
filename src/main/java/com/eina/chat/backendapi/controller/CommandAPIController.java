package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.message.request.*;
import com.eina.chat.backendapi.protocol.packages.message.response.FileFromRoomResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.FileFromUserResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.MessageFromRoomResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.MessageFromUserResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.rabbitmq.ReceiveHandler;
import com.eina.chat.backendapi.service.*;
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
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.util.List;

@Controller
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
    private UserAccountDatabaseAPI userAccountDatabaseAPI;

    @Autowired
    private GroupsManagementDatabaseAPI groupsManagementDatabaseAPI;

    @Autowired
    private MessageHistoryDatabaseAPI messageHistoryDatabaseAPI;

    /**
     * Logger
     */
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(CommandAPIController.class);

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
                !userAccountDatabaseAPI.checkUserExist(addUserToChatRoomCommand.getUsername()))
            return new OperationFailResponse(addUserToChatRoomCommand.getMessageId(), "Non-existent user or room");

        else if (!groupsManagementDatabaseAPI.checkIfIsGroupAdmin(username, addUserToChatRoomCommand.getRoomName()))
            return new OperationFailResponse(addUserToChatRoomCommand.getMessageId(), "You are not the room admin");

        else {
            groupsManagementDatabaseAPI.addUserToGroup(addUserToChatRoomCommand.getUsername(), addUserToChatRoomCommand.getRoomName()
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
                maxRoomLength < createRoomCommand.getRoomName().length())
            return new OperationFailResponse(createRoomCommand.getMessageId(), "Room name must have between " +
                    minRoomLength.toString() + " and " + maxRoomLength.toString() + " characters");

        else if (groupsManagementDatabaseAPI.checkIfGroupExist(createRoomCommand.getRoomName()))
            return new OperationFailResponse(createRoomCommand.getMessageId(), "The name of the room already exists");

        else {
            groupsManagementDatabaseAPI.createGroup(username, createRoomCommand.getRoomName());
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
        List<String> administeredGroups = groupsManagementDatabaseAPI.getAllGroupsWhereIsAdmin(username);
        for (String group : administeredGroups) {
            // TODO: Send message group members informing the deletion

            // Delete associated broker
            messageBrokerAPI.deleteGroup(group);
        }

        // Delete user account
        userAccountDatabaseAPI.deleteUser(username);

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
        // TODO: May notify all group member of the deletion
        logger.info("Received message from type DeleteRoomCommand from: " + username);
        if (deleteRoomCommand.getRoomName() == null ||
                !groupsManagementDatabaseAPI.checkIfIsGroupAdmin(username, deleteRoomCommand.getRoomName()))
            return new OperationFailResponse(deleteRoomCommand.getMessageId(), "You are not the room admin");

        else {
            // Delete group
            groupsManagementDatabaseAPI.deleteGroup(deleteRoomCommand.getRoomName());

            // Delete group from broker
            messageBrokerAPI.deleteGroup(deleteRoomCommand.getRoomName());

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
        // TODO: May notify of the deletion
        logger.info("Received message from type DeleteUserFromChatRoom from: " + username);
        if (removeUserFromChatRoom.getRoomName() == null || removeUserFromChatRoom.getUsername() == null)
            return new OperationFailResponse(removeUserFromChatRoom.getMessageId(), "Non-existent user or room");

        else if (!groupsManagementDatabaseAPI.checkIfIsGroupAdmin(username, removeUserFromChatRoom.getRoomName()))
            return new OperationFailResponse(removeUserFromChatRoom.getMessageId(), "You are not the room admin");

        else if (!groupsManagementDatabaseAPI.checkIfIsGroupMember(removeUserFromChatRoom.getUsername(), removeUserFromChatRoom.getRoomName()))
            return new OperationFailResponse(removeUserFromChatRoom.getMessageId(), "Non-existent user in the room");

        else if (groupsManagementDatabaseAPI.checkIfIsGroupAdmin(removeUserFromChatRoom.getUsername(), removeUserFromChatRoom.getRoomName()))
            return new OperationFailResponse(removeUserFromChatRoom.getMessageId(), "You can not remove yourself from the room");

        else {
            groupsManagementDatabaseAPI.removeUserFromGroup(removeUserFromChatRoom.getUsername(), removeUserFromChatRoom.getRoomName()
            );
            messageBrokerAPI.removeUserFromGroup(removeUserFromChatRoom.getUsername(), removeUserFromChatRoom.getRoomName());
            return new OperationSucceedResponse(removeUserFromChatRoom.getMessageId());
        }
    }

//     This call looks unnecessary, wait until client been implemented to delete
//    /**
//     * Handle messages received from user with content of type SearchUserCommand
//     *
//     * @param username          user username
//     * @param searchUserCommand content
//     * @return command response
//     */
//    public BasicPackage handlerSearchUserCommand(String username, SearchUserCommand searchUserCommand) {
//        logger.info("Received message from type SearchUserCommand from: " + username);
//
//        return new OperationFailResponse(searchUserCommand.getMessageId(), "Unknown command");
//    }

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

        else if (!groupsManagementDatabaseAPI.checkIfIsGroupMember(username, sendFileToRoomCommand.getRoomName()))
            return new OperationFailResponse(sendFileToRoomCommand.getMessageId(), "You are not member of the room");

        else if (sendFileToRoomCommand.getFile() == null || sendFileToRoomCommand.getFile().length == 0 ||
                sendFileToRoomCommand.getFile().length > maxFileSize * 1000000)
            return new OperationFailResponse(sendFileToRoomCommand.getMessageId(), "File must have between 1 and "
                    + (maxFileSize * 1000000) + " bytes");

        else {
            messageBrokerAPI.sendFileToGroup(username, sendFileToRoomCommand.getRoomName(),
                    encryptionAPI.symmetricEncryptFile(sendFileToRoomCommand.getFile()));
            messageHistoryDatabaseAPI.saveFileToGroup(username, sendFileToRoomCommand.getRoomName(),
                    encryptionAPI.symmetricEncryptFile(sendFileToRoomCommand.getFile()));
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
        if (sendFileToUserCommand.getUsername() == null || !userAccountDatabaseAPI.checkUserExist(sendFileToUserCommand.getUsername()))
            return new OperationFailResponse(sendFileToUserCommand.getMessageId(), "Non-existent user");

        else if (sendFileToUserCommand.getFile() == null || sendFileToUserCommand.getFile().length == 0 ||
                sendFileToUserCommand.getFile().length > maxFileSize * 1000000)
            return new OperationFailResponse(sendFileToUserCommand.getMessageId(), "File must have between 1 and "
                    + (maxFileSize * 1000000) + " bytes");

        else {
            messageBrokerAPI.sendFileToUser(username, sendFileToUserCommand.getUsername(),
                    encryptionAPI.symmetricEncryptFile(sendFileToUserCommand.getFile()));
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

        else if (!groupsManagementDatabaseAPI.checkIfIsGroupMember(username, sendMessageToRoomCommand.getRoomName()))
            return new OperationFailResponse(sendMessageToRoomCommand.getMessageId(), "You are not member of the room");

        else if (sendMessageToRoomCommand.getMessage() == null || sendMessageToRoomCommand.getMessage().isBlank() ||
                sendMessageToRoomCommand.getMessage().length() > maxMessageLength)
            return new OperationFailResponse(sendMessageToRoomCommand.getMessageId(), "Message must have between 1 and "
                    + maxMessageLength + " characters");

        else {
            messageBrokerAPI.sendMessageToGroup(username, sendMessageToRoomCommand.getRoomName(),
                    encryptionAPI.symmetricEncryptString(sendMessageToRoomCommand.getMessage()));
            messageHistoryDatabaseAPI.saveMessageFromGroup(username, sendMessageToRoomCommand.getRoomName(),
                    encryptionAPI.symmetricEncryptString(sendMessageToRoomCommand.getMessage()));
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
        if (sendMessageToUserCommand.getUsername() == null || !userAccountDatabaseAPI.checkUserExist(sendMessageToUserCommand.getUsername()))
            return new OperationFailResponse(sendMessageToUserCommand.getMessageId(), "Non-existent user");

        else if (sendMessageToUserCommand.getMessage() == null || sendMessageToUserCommand.getMessage().isBlank() ||
                sendMessageToUserCommand.getMessage().length() > maxMessageLength)
            return new OperationFailResponse(sendMessageToUserCommand.getMessageId(), "Message must have between 1 and "
                    + maxMessageLength + " characters");

        else {
            messageBrokerAPI.sendMessageToUser(username, sendMessageToUserCommand.getUsername(),
                    encryptionAPI.symmetricEncryptString(sendMessageToUserCommand.getMessage()));
            return new OperationSucceedResponse(sendMessageToUserCommand.getMessageId());
        }
    }

//    This call looks unnecessary, wait until client been implemented but divide it in two calls looks more appropriate
//    /**
//     * Handle messages received from user with content of type SendMessageToUserCommand
//     *
//     * @param username                      user username
//     * @param joinedRoomsChatHistoryCommand content
//     * @return command response
//     */
//    public BasicPackage handlerJoinedRoomsChatHistoryCommand(String username, JoinedRoomsChatHistoryCommand joinedRoomsChatHistoryCommand) {
//        logger.info("Received message from type JoinedRoomsChatHistoryCommand from: " + username);
//
//        // getGroupsFromUser
//        // getHistoryFromGroup
//        return new OperationFailResponse(joinedRoomsChatHistoryCommand.getMessageId(), "Unknown command");
//    }

    /**
     * Notify when user is subscribed to /user/queue/message
     */
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        simpDestination = simpDestination != null ? simpDestination : "";

        String username = event.getUser() != null ? event.getUser().getName() : "";

        // Log
        logger.info("Session Subscribe Event on endpoint: " + simpDestination + " by user: " + username);

        if (simpDestination.equals("/user/queue/message") && !username.isBlank()) {
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

            });
        }
    }

    /**
     * Notify when user is unsubscribed to /user/queue/message
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
            messageBrokerAPI.deleteUserReceiverMessagesCallback(username);
        }
    }
}
