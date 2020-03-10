package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.errors.WSResponseStatus;
import com.eina.chat.backendapi.exceptions.ClientProducedException;
import com.eina.chat.backendapi.exceptions.DatabaseInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

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
     * Send error to subscriber
     *
     * @param error                 error to send to subscriber
     * @param sessionId             session if of the user that should receive the original url
     * @param simpMessagingTemplate a SimpMessagingTemplate instance to perform the call
     */
    private void sendErrorToSubscriber(String sessionId, String error,
                                       SimpMessagingTemplate simpMessagingTemplate) {

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(sessionId,
                "/queue/error/send-direct-message",
                new WSResponseStatus(WSResponseStatus.StatusType.ERROR, error),
                headerAccessor.getMessageHeaders());
    }

    @MessageMapping("/send-direct-message")
    @SendToUser("/queue/error/send-direct-message")
    public WSResponseStatus userSendMessageToUser(String user, @Header("simpSessionId") String simpSessionId)
            throws ClientProducedException, DatabaseInternalException {
        // TODO: Implement method
        return new WSResponseStatus(WSResponseStatus.StatusType.SUCCESS , "Message sent");
    }


    /**
     * Catch /send-direct-message produced Exceptions
     *
     * @param e exception captured
     */
    @SuppressWarnings("unused")
    @MessageExceptionHandler(ClientProducedException.class)
    public void userErrorHandlerGetInfo(Exception e) {
        // User error
        ClientProducedException ex = (ClientProducedException) e;
        sendErrorToSubscriber(ex.getSimpSessionId(), ex.getMessage(), simpMessagingTemplate);
    }

    /**
     * Catch /send-direct-message internal produced Exceptions
     *
     * @param e exception captured
     */
    @SuppressWarnings("unused")
    @MessageExceptionHandler({DatabaseInternalException.class, InterruptedException.class})
    public void internalErrorHandlerGetInfo(Exception e) {
        // Server error
        logger.error(e.getMessage());
    }
}
