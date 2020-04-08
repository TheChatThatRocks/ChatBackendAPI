package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.errors.WSResponseStatus;
import com.eina.chat.backendapi.exceptions.ClientProducedException;
import com.eina.chat.backendapi.exceptions.DatabaseInternalException;
import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
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
    private static Logger logger = LoggerFactory.getLogger(SignUpController.class);

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

//    /**
//     * Send error to subscriber
//     *
//     * @param error                 error to send to subscriber
//     * @param sessionId             session if of the user that should receive the original url
//     * @param simpMessagingTemplate a SimpMessagingTemplate instance to perform the call
//     */
//    private void sendErrorToSubscriber(String sessionId, String error,
//                                       SimpMessagingTemplate simpMessagingTemplate) {
//
//        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
//                .create(SimpMessageType.MESSAGE);
//        headerAccessor.setSessionId(sessionId);
//        headerAccessor.setLeaveMutable(true);
//
//        simpMessagingTemplate.convertAndSendToUser(sessionId,
//                "/queue/error/send-direct-message",
//                new WSResponseStatus(WSResponseStatus.StatusType.ERROR, error),
//                headerAccessor.getMessageHeaders());
//    }

    @MessageMapping("/message")
    @SendToUser("/queue/error/message")
    public BasicPackage userSendMessageToUser(@Payload SendMessageToUser sendMessageToUser, Principal principal) {
        logger.info("Mensaje ha llegado al endpoint mensajes -----------------------------");
        if (messageBrokerAPI.sendMessageToUser(principal.getName(), sendMessageToUser.getUsername(), sendMessageToUser.getMessage())) {
            return new ErrorMessage(TypeOfMessage.OPERATION_SUCCEED, sendMessageToUser.getMessageId(), "OK");
        } else {
            return new ErrorMessage(TypeOfMessage.SEND_MESSAGE_TO_USER_ERROR, sendMessageToUser.getMessageId(), "Not found");
        }
    }
//
//
//    /**
//     * Catch /send-direct-message produced Exceptions
//     *
//     * @param e exception captured
//     */
//    @SuppressWarnings("unused")
//    @MessageExceptionHandler(ClientProducedException.class)
//    public void userErrorHandlerGetInfo(Exception e) {
//        // User error
//        ClientProducedException ex = (ClientProducedException) e;
//        sendErrorToSubscriber(ex.getSimpSessionId(), ex.getMessage(), simpMessagingTemplate);
//    }
//
//    /**
//     * Catch /send-direct-message internal produced Exceptions
//     *
//     * @param e exception captured
//     */
//    @SuppressWarnings("unused")
//    @MessageExceptionHandler({DatabaseInternalException.class, InterruptedException.class})
//    public void internalErrorHandlerGetInfo(Exception e) {
//        // Server error
//        logger.error(e.getMessage());
//    }

    /**
     * Notify when user is subscribed to /user/queue/message
     */
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        final String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        final String simpSessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        final Principal user = event.getUser();

        if (simpDestination != null && simpDestination.equals("/user/queue/message") && simpSessionId != null
                && user != null) {

            String username = user.getName();
            logger.info("Session ids buenos " + simpSessionId + " " + username);

            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(simpSessionId);
            headerAccessor.setLeaveMutable(true);

            logger.info("Mensaje se ha enviado desde onUserMessageArrive -----------------------------" + simpSessionId);

            simpMessagingTemplate.convertAndSendToUser(simpSessionId, "/queue/message",
                    new MessageFromUser(username, "prueba"), headerAccessor.getMessageHeaders());

//            messageBrokerAPI.addUserReceiverMessagesCallback(new MessageBrokerAPI.BrokerMessagePackage() {
//                @Override
//                public void onUserMessageArrive(String username, String message) {
//                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
//                    headerAccessor.setSessionId(simpSessionId);
//                    headerAccessor.setLeaveMutable(true);
//
//                    logger.info("Mensaje se ha enviado desde onUserMessageArrive -----------------------------" + simpSessionId);
//
//                    simpMessagingTemplate.convertAndSendToUser(simpSessionId, "/queue/message",
//                            new MessageFromUser(username, message), headerAccessor.getMessageHeaders());
//                }
//
//                @Override
//                public void onGroupMessageArrive(String user, String group, String message) {
//                    // TODO: Complete
//                }
//
//            }, username);
        }
    }
}
