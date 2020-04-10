package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.model.User;
import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.signup.request.AddAccountCommand;
import com.eina.chat.backendapi.protocol.packages.signup.response.SignUpErrorResponse;
import com.eina.chat.backendapi.protocol.packages.signup.response.SignUpSuccessResponse;
import com.eina.chat.backendapi.service.UserAccountDatabaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class SignUpController {
    /**
     * User account database API
     */
    @Autowired
    private UserAccountDatabaseAPI userAccountDatabaseAPI;

    @MessageMapping("/sign-up")
    @SendToUser("/queue/error/sign-up")
    public BasicPackage signUpUser(BasicPackage basicPackage) {
        if (basicPackage instanceof AddAccountCommand) {
            AddAccountCommand addAccountCommand = (AddAccountCommand) basicPackage;
            if (userAccountDatabaseAPI.createUser(new User(addAccountCommand.getUsername(), addAccountCommand.getPassword())))
                return new SignUpSuccessResponse(basicPackage.getMessageId());
            else
                return new SignUpErrorResponse(basicPackage.getMessageId(), "Duplicated account");
        } else
            return new SignUpErrorResponse(basicPackage.getMessageId(), "Bad message format");
    }
}
