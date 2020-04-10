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

    @MessageMapping("/message")
    @SendToUser("/queue/error/message")
    public BasicPackage commandAPIMessageHandler(@Payload BasicPackage basicPackage, Principal principal) {
        if (basicPackage instanceof SendMessageToUserCommand) {
            SendMessageToUserCommand sendMessageToUser = (SendMessageToUserCommand) basicPackage;
            if (userAccountDatabaseAPI.checkUserExist(sendMessageToUser.getUsername())) {
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
        final String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        final Principal user = event.getUser();

        if (simpDestination != null && simpDestination.equals("/user/queue/message") && user != null) {
            final String username = user.getName();
            messageBrokerAPI.addUserReceiverMessagesCallback(username, new ReceiveHandler() {
                @Override
                public void onUserMessageArrive(String fromUsername, String message) {
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
        final String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        final Principal user = event.getUser();

        if (simpDestination != null && simpDestination.equals("/user/queue/message") && user != null) {
            final String username = user.getName();
            messageBrokerAPI.deleteUserReceiverMessagesCallback(username);
        }
    }
}
