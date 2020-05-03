package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.admin.request.SendMessageToAllCommand;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class AdminAPIController {
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
     * @return command response
     */
    @MessageMapping("/admin")
    @SendToUser("/queue/error/admin")
    public BasicPackage adminCommandAPIMessageHandler(@Payload BasicPackage basicPackage) {
        // Select correct handler for the type of message
        if (basicPackage instanceof SendMessageToAllCommand)
            return handlerSendMessageToAllCommand((SendMessageToAllCommand) basicPackage);
        else return new OperationFailResponse(basicPackage.getMessageId(), "Unknown command");
    }

    /**
     * Handle messages received from admin with content of type SendMessageToAllCommand
     *
     * @param sendMessageToAllCommand  content
     * @return command response
     */
    public BasicPackage handlerSendMessageToAllCommand(SendMessageToAllCommand sendMessageToAllCommand) {
        logger.info("Received message from type SendMessageToAllCommand");
        if (sendMessageToAllCommand.getMessage() == null || sendMessageToAllCommand.getMessage().isBlank() ||
                sendMessageToAllCommand.getMessage().length() > maxMessageLength)
            return new OperationFailResponse(sendMessageToAllCommand.getMessageId(), "Message must have between 1 and "
                    + maxMessageLength + " characters");
        else {
            messageBrokerAPI.sendMessageToUser("admin", "bcast",
                    encryptionAPI.symmetricEncryptString(sendMessageToAllCommand.getMessage()));
            return new OperationSucceedResponse(sendMessageToAllCommand.getMessageId());
        }
    }
}
