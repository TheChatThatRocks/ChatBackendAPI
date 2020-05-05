package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.admin.request.SendMessageToAllCommand;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
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

@Controller
public class AdminAPIController {

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
     * Max message length
     */
    @Value("${app.max-message-length:}")
    private Integer maxMessageLength;


    /**
     * Command endpoint
     *
     * @param basicPackage command and arguments
     * @param principal    user info
     * @return command response
     */
    @MessageMapping("/admin")
    @SendToUser("/queue/error/admin")
    public BasicPackage commandAPIMessageHandler(@Payload BasicPackage basicPackage, Principal principal) {
        // Select correct handler for the type of message
        if (basicPackage instanceof SendMessageToAllCommand)
            return handlerSendMessageToAllCommand((SendMessageToAllCommand) basicPackage);
        else return new OperationFailResponse(basicPackage.getMessageId(), "Unknown command");
    }

    /**
     * Handle messages received from admin with content of type SendMessageToAllCommand
     *
     * @param sendMessageToAllCommand content
     * @return command response
     */
    public BasicPackage handlerSendMessageToAllCommand(SendMessageToAllCommand sendMessageToAllCommand) {
        logger.info("Received message from type SendMessageToAllCommand");
        if (sendMessageToAllCommand.getMessage() == null || sendMessageToAllCommand.getMessage().isBlank() ||
                sendMessageToAllCommand.getMessage().length() > maxMessageLength)
            return new OperationFailResponse(sendMessageToAllCommand.getMessageId(), "Message must have between 1 and "
                    + maxMessageLength + " characters");
        else {
            // TODO: create a admin queue?
            messageBrokerAPI.sendMessageToUser("admin", "bcast",
                    encryptionAPI.symmetricEncryptString(sendMessageToAllCommand.getMessage()));
            return new OperationSucceedResponse(sendMessageToAllCommand.getMessageId());
        }
    }


    /**
     * Notify when admin is subscribed to /user/queue/stats
     */
    /*
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
    */

    /**
     * Notify when user is unsubscribed to /user/queue/message
     */
    /*
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
    */
}
