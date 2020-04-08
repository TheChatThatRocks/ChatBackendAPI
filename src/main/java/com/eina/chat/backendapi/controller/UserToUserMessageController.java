package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
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
public class UserToUserMessageController {
    /**
     * Logger instance
     */
    final private static Logger logger = LoggerFactory.getLogger(UserToUserMessageController.class);

    /**
     * Simple messaging template
     */
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * RabbitMQ API
     */
    @Autowired
    private MessageBrokerAPI messageBrokerAPI;

    @MessageMapping("/message")
    @SendToUser("/queue/error/message")
    public BasicPackage userSendMessageToUser(@Payload SendMessageToUser sendMessageToUser, Principal principal) {
        if (messageBrokerAPI.sendMessageToUser(principal.getName(), sendMessageToUser.getUsername(), sendMessageToUser.getMessage())) {
            return new ErrorResponse(TypeOfMessage.OPERATION_SUCCEED, sendMessageToUser.getMessageId(), "OK");
        } else {
            return new ErrorResponse(TypeOfMessage.SEND_MESSAGE_TO_USER_ERROR, sendMessageToUser.getMessageId(), "Not found");
        }
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
            messageBrokerAPI.addUserReceiverMessagesCallback(new MessageBrokerAPI.BrokerMessagePackage() {
                @Override
                public void onUserMessageArrive(String fromUsername, String message) {
                    simpMessagingTemplate.convertAndSendToUser(username,
                            "/queue/message",
                            new MessageFromUser(fromUsername, message));
                }

                @Override
                public void onGroupMessageArrive(String user, String group, String message) {
                    // TODO: Complete
                }

            }, username);
        }
    }
}
