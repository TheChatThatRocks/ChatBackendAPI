package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.message.request.*;
import com.eina.chat.backendapi.protocol.packages.message.response.MessageFromUserResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.SendMessageToUserErrorResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.UnknownCommandResponse;
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
     * @param basicPackage command and arguments
     * @param principal user info
     * @return command response
     */
    @MessageMapping("/message")
    @SendToUser("/queue/error/message")
    public BasicPackage commandAPIMessageHandler(@Payload BasicPackage basicPackage, Principal principal) {
        // Select correct handler for the type of message
        if (basicPackage instanceof AddUserToChatRoom)
            return handlerAddUserToChatRoom(principal.getName(), (AddUserToChatRoom) basicPackage);
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
        else return new UnknownCommandResponse(basicPackage.getMessageId());
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username          user username
     * @param addUserToChatRoom content
     * @return command response
     */
    public BasicPackage handlerAddUserToChatRoom(String username, AddUserToChatRoom addUserToChatRoom) {
        // TODO:
        return new UnknownCommandResponse(addUserToChatRoom.getMessageId());
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username          user username
     * @param createRoomCommand content
     * @return command response
     */
    public BasicPackage handlerCreateRoomCommand(String username, CreateRoomCommand createRoomCommand) {
        // TODO:
        return new UnknownCommandResponse(createRoomCommand.getMessageId());
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username             user username
     * @param deleteAccountCommand content
     * @return command response
     */
    public BasicPackage handlerDeleteAccountCommand(String username, DeleteAccountCommand deleteAccountCommand) {
        // TODO:
        return new UnknownCommandResponse(deleteAccountCommand.getMessageId());
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username          user username
     * @param deleteRoomCommand content
     * @return command response
     */
    public BasicPackage handlerDeleteRoomCommand(String username, DeleteRoomCommand deleteRoomCommand) {
        // TODO:
        return new UnknownCommandResponse(deleteRoomCommand.getMessageId());
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username               user username
     * @param deleteUserFromChatRoom content
     * @return command response
     */
    public BasicPackage handlerDeleteUserFromChatRoom(String username, DeleteUserFromChatRoom deleteUserFromChatRoom) {
        // TODO:
        return new UnknownCommandResponse(deleteUserFromChatRoom.getMessageId());
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username          user username
     * @param searchUserCommand content
     * @return command response
     */
    public BasicPackage handlerSearchUserCommand(String username, SearchUserCommand searchUserCommand) {
        // TODO:
        return new UnknownCommandResponse(searchUserCommand.getMessageId());
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username              user username
     * @param sendFileToRoomCommand content
     * @return command response
     */
    public BasicPackage handlerSendFileToRoomCommand(String username, SendFileToRoomCommand sendFileToRoomCommand) {
        // TODO:
        return new UnknownCommandResponse(sendFileToRoomCommand.getMessageId());
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username              user username
     * @param sendFileToUserCommand content
     * @return command response
     */
    public BasicPackage handlerSendFileToUserCommand(String username, SendFileToUserCommand sendFileToUserCommand) {
        // TODO:
        return new UnknownCommandResponse(sendFileToUserCommand.getMessageId());
    }

    /**
     * Handle messages received from user with content of type AddUserToChatRoom
     *
     * @param username                 user username
     * @param sendMessageToRoomCommand content
     * @return command response
     */
    public BasicPackage handlerSendMessageToRoomCommand(String username, SendMessageToRoomCommand sendMessageToRoomCommand) {
        // TODO:
        return new UnknownCommandResponse(sendMessageToRoomCommand.getMessageId());
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
        if (userAccountDatabaseAPI.checkUserExist(sendMessageToUserCommand.getUsername())) {
            messageBrokerAPI.sendMessageToUser(username, sendMessageToUserCommand.getUsername(),
                    encryptionAPI.symmetricEncryptString(sendMessageToUserCommand.getMessage()));
            return new OperationSucceedResponse(sendMessageToUserCommand.getMessageId());
        } else
            return new SendMessageToUserErrorResponse(sendMessageToUserCommand.getMessageId());
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
                public void onGroupMessageArrive(String user, String group, String message) {
                    // TODO: Complete
                }

                @Override
                public void onUserFileArrive(String username, byte[] file) {
                    // TODO: Complete
                }

                @Override
                public void onGroupFileArrive(String username, String group, byte[] file) {
                    // TODO: Complete
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
