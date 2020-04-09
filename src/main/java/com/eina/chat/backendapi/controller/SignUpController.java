package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.model.User;
import com.eina.chat.backendapi.protocol.packages.*;
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
    public ErrorResponse signUpUser(SendCommandPackage sendCommandPackage) {
        if (sendCommandPackage.getTypeOfMessage() == TypeOfMessage.ADD_ACCOUNT && sendCommandPackage.getArgument() instanceof AddAccountArgument) {
            if (userAccountDatabaseAPI.createUser(new User(((AddAccountArgument) sendCommandPackage.getArgument()).getUsername(), ((AddAccountArgument) sendCommandPackage.getArgument()).getPassword()))) {
                return new ErrorResponse(TypeOfMessage.SIGN_UP_SUCCESS, sendCommandPackage.getMessageId(), "OK");
            } else {
                return new ErrorResponse(TypeOfMessage.SIGN_UP_ERROR, sendCommandPackage.getMessageId(), "Duplicated account");
            }
        } else {
            return new ErrorResponse(TypeOfMessage.SIGN_UP_ERROR, sendCommandPackage.getMessageId(), "Duplicated account");
        }
    }
}
