package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.model.User;
import com.eina.chat.backendapi.protocol.packages.AddAccount;
import com.eina.chat.backendapi.protocol.packages.ErrorResponse;
import com.eina.chat.backendapi.protocol.packages.TypeOfMessage;
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
    public ErrorResponse signUpUser(AddAccount addAccount) {
        if (userAccountDatabaseAPI.createUser(new User(addAccount.getUsername(), addAccount.getPassword()))) {
            return new ErrorResponse(TypeOfMessage.SIGN_UP_SUCCESS, addAccount.getMessageId(), "OK");
        } else {
            return new ErrorResponse(TypeOfMessage.SIGN_UP_ERROR, addAccount.getMessageId(), "Duplicated account");
        }
    }
}
