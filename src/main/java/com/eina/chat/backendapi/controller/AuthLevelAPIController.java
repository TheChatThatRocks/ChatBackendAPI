package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.message.request.GetAuthLevelCommand;
import com.eina.chat.backendapi.protocol.packages.message.response.AuthLevelResponse;
import com.eina.chat.backendapi.service.PersistentDataAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;

import java.security.Principal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthLevelAPIController {
    /**
     * Simple messaging template
     */
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Database API
     */
    @Autowired
    private PersistentDataAPI persistentDataAPI;

    /**
     * Logger
     */
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(AuthLevelAPIController.class);

    /**
     * Executor user for async tasks
     */
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * Command endpoint
     *
     * @param basicPackage command and arguments
     * @param principal    user info
     * @return command response
     */
    @MessageMapping("/auth-level")
    @SendToUser("/queue/error/auth-level")
    public BasicPackage accessLevelAPIMessageHandler(@Payload BasicPackage basicPackage, Principal principal) {
        // Select correct handler for the type of message
        if (basicPackage instanceof GetAuthLevelCommand)
            return handlerGetAuthLevelCommand(principal.getName(), (GetAuthLevelCommand) basicPackage);
        else
            return new OperationFailResponse(basicPackage.getMessageId(), "Unknown command");
    }

    /**
     * Handle messages received from user with content of type GetAuthLevelCommand
     *
     * @param username            user username
     * @param getAuthLevelCommand content
     * @return command response
     */
    public BasicPackage handlerGetAuthLevelCommand(String username, GetAuthLevelCommand getAuthLevelCommand) {
        logger.info("Received message from type GetAuthLevelCommand from: " + username);
        executorService.submit(() -> simpMessagingTemplate.convertAndSendToUser(username, "/queue/auth-level",
                new AuthLevelResponse(getAuthLevelCommand.getMessageId(),
                        persistentDataAPI.getUserRole(username))));
        return new OperationSucceedResponse(getAuthLevelCommand.getMessageId());
    }

}
