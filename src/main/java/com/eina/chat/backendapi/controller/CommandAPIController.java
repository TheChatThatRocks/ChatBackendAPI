package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.message.request.SendMessageToUserCommand;
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

    @MessageMapping("/message")
    @SendToUser("/queue/error/message")
    public BasicPackage commandAPIMessageHandler(@Payload BasicPackage basicPackage, Principal principal) {
        if (basicPackage instanceof SendMessageToUserCommand) {
            SendMessageToUserCommand sendMessageToUser = (SendMessageToUserCommand) basicPackage;

            if (userAccountDatabaseAPI.checkUserExist(sendMessageToUser.getUsername())) {
                System.out.println("Message send en api -----------" + sendMessageToUser.getUsername());
                messageBrokerAPI.sendMessageToUser(principal.getName(), sendMessageToUser.getUsername(),
                        sendMessageToUser.getMessage());
                return new OperationSucceedResponse(sendMessageToUser.getMessageId());
            } else
                return new SendMessageToUserErrorResponse(sendMessageToUser.getMessageId());

        } else return new UnknownCommandResponse(basicPackage.getMessageId());
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
                            new MessageFromUserResponse(fromUsername, message));
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
    public void handleSessionUnsubscribeEvent(SessionSubscribeEvent event) {
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
