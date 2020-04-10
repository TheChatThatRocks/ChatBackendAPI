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
import com.eina.chat.backendapi.service.EncryptionAPI;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import com.eina.chat.backendapi.service.UserAccountDatabaseAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;

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
    // TODO: Add encryption before comunications
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

    /**
     * Logger
     */
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(CommandAPIController.class);

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
        else if (basicPackage instanceof DeleteUserFromChatRoom)
            return handlerDeleteUserFromChatRoom(principal.getName(), (DeleteUserFromChatRoom) basicPackage);
        else if (basicPackage instanceof SearchUserCommand)
            return handlerSearchUserCommand(principal.getName(), (SearchUserCommand) basicPackage);
        else if (basicPackage instanceof SendFileToRoomCommand)
            return handlerSendFileToRoomCommand(principal.getName(), (SendFileToRoomCommand) basicPackage);
        else if (basicPackage instanceof SendFileToUserCommand)
            return handlerSendFileToUserCommand(principal.getName(), (SendFileToUserCommand) basicPackage);
        else if (basicPackage instanceof SendMessageToRoomCommand)
            return handlerSendMessageToRoomCommand(principal.getName(), (SendMessageToRoomCommand) basicPackage);
        else if (basicPackage instanceof SendMessageToUserCommand)
            return handlerSendMessageToUserCommand(principal.getName(), (SendMessageToUserCommand) basicPackage);
        else if (basicPackage instanceof JoinedRoomsChatHistoryCommand)
            return handlerJoinedRoomsChatHistoryCommand(principal.getName(), (JoinedRoomsChatHistoryCommand) basicPackage);
        else return new OperationFailResponse(basicPackage.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username                 user username
     * @param addUserToChatRoomCommand content
     * @return command response
     */
    public BasicPackage handlerAddUserToChatRoom(String username, AddUserToChatRoomCommand addUserToChatRoomCommand) {
        logger.info("Received message from type AddUserToChatRoomCommand from: " + username);
        // TODO:
        return new OperationFailResponse(addUserToChatRoomCommand.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username          user username
     * @param createRoomCommand content
     * @return command response
     */
    public BasicPackage handlerCreateRoomCommand(String username, CreateRoomCommand createRoomCommand) {
        logger.info("Received message from type CreateRoomCommand from: " + username);
        // TODO:
        return new OperationFailResponse(createRoomCommand.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username             user username
     * @param deleteAccountCommand content
     * @return command response
     */
    public BasicPackage handlerDeleteAccountCommand(String username, DeleteAccountCommand deleteAccountCommand) {
        logger.info("Received message from type DeleteAccountCommand from: " + username);
        // TODO:
        return new OperationFailResponse(deleteAccountCommand.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username          user username
     * @param deleteRoomCommand content
     * @return command response
     */
    public BasicPackage handlerDeleteRoomCommand(String username, DeleteRoomCommand deleteRoomCommand) {
        logger.info("Received message from type DeleteRoomCommand from: " + username);
        // TODO:
        return new OperationFailResponse(deleteRoomCommand.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username               user username
     * @param deleteUserFromChatRoom content
     * @return command response
     */
    public BasicPackage handlerDeleteUserFromChatRoom(String username, DeleteUserFromChatRoom deleteUserFromChatRoom) {
        logger.info("Received message from type DeleteUserFromChatRoom from: " + username);
        // TODO:
        return new OperationFailResponse(deleteUserFromChatRoom.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username          user username
     * @param searchUserCommand content
     * @return command response
     */
    public BasicPackage handlerSearchUserCommand(String username, SearchUserCommand searchUserCommand) {
        logger.info("Received message from type SearchUserCommand from: " + username);
        // TODO:
        return new OperationFailResponse(searchUserCommand.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username              user username
     * @param sendFileToRoomCommand content
     * @return command response
     */
    public BasicPackage handlerSendFileToRoomCommand(String username, SendFileToRoomCommand sendFileToRoomCommand) {
        logger.info("Received message from type SendFileToRoomCommand from: " + username);
        // TODO:
        return new OperationFailResponse(sendFileToRoomCommand.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username              user username
     * @param sendFileToUserCommand content
     * @return command response
     */
    public BasicPackage handlerSendFileToUserCommand(String username, SendFileToUserCommand sendFileToUserCommand) {
        logger.info("Received message from type SendFileToUserCommand from: " + username);
        // TODO:
        return new OperationFailResponse(sendFileToUserCommand.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username                 user username
     * @param sendMessageToRoomCommand content
     * @return command response
     */
    public BasicPackage handlerSendMessageToRoomCommand(String username, SendMessageToRoomCommand sendMessageToRoomCommand) {
        logger.info("Received message from type SendMessageToRoomCommand from: " + username);
        // TODO:
        return new OperationFailResponse(sendMessageToRoomCommand.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
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
        if (userAccountDatabaseAPI.checkUserExist(sendMessageToUserCommand.getUsername()) &&
                !sendMessageToUserCommand.getMessage().isBlank() &&
                sendMessageToUserCommand.getMessage().length() <= 500) {
            messageBrokerAPI.sendMessageToUser(username, sendMessageToUserCommand.getUsername(),
                    encryptionAPI.symmetricEncryptString(sendMessageToUserCommand.getMessage()));
            return new OperationSucceedResponse(sendMessageToUserCommand.getMessageId());
        } else
            return new OperationFailResponse(sendMessageToUserCommand.getMessageId(), TypesOfMessage.SEND_MESSAGE_TO_USER_ERROR);
    }

    /**
     * Handle messages received from user with content of type SendMessageToUserCommand
     *
     * @param username                      user username
     * @param joinedRoomsChatHistoryCommand content
     * @return command response
     */
    public BasicPackage handlerJoinedRoomsChatHistoryCommand(String username, JoinedRoomsChatHistoryCommand joinedRoomsChatHistoryCommand) {
        logger.info("Received message from type JoinedRoomsChatHistoryCommand from: " + username);
        // TODO:
        return new OperationFailResponse(joinedRoomsChatHistoryCommand.getMessageId(), TypesOfMessage.UNKNOWN_COMMAND);
    }

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
                    logger.debug("User message arrive callback with message from: " + fromUsername);
                    simpMessagingTemplate.convertAndSendToUser(username,
                            "/queue/message",
                            new MessageFromUserResponse(fromUsername, encryptionAPI.symmetricDecryptString(message)));
                }

                @Override
                public void onGroupMessageArrive(String fromUsername, String group, String message) {
                    logger.debug("Group message arrive callback with message from: " + fromUsername + " and group:" + group);
                    simpMessagingTemplate.convertAndSendToUser(username,
                            "/queue/message",
                            new MessageFromRoomResponse(fromUsername, group, encryptionAPI.symmetricDecryptString(message)));
                }

                @Override
                public void onUserFileArrive(String fromUsername, byte[] file) {
                    logger.debug("User file arrive callback with message from: " + fromUsername);
                    simpMessagingTemplate.convertAndSendToUser(username,
                            "/queue/message",
                            new FileFromUserResponse(fromUsername, encryptionAPI.symmetricDecryptFile(file)));
                }

                @Override
                public void onGroupFileArrive(String fromUsername, String group, byte[] file) {
                    logger.debug("Group file arrive callback with message from: " + fromUsername + " and group:" + group);
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
