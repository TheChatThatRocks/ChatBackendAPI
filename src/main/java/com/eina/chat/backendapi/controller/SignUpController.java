package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.model.User;
import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.signup.request.AddAccountCommand;
import com.eina.chat.backendapi.protocol.packages.signup.response.SignUpErrorResponse;
import com.eina.chat.backendapi.protocol.packages.signup.response.SignUpSuccessResponse;
import com.eina.chat.backendapi.security.AccessLevels;
import com.eina.chat.backendapi.service.EncryptionAPI;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
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

    /**
     * Encryption API
     */
    @Autowired
    private EncryptionAPI encryptionAPI;

    /**
     * Broker API
     */
    @Autowired
    private MessageBrokerAPI messageBrokerAPI;

    @MessageMapping("/sign-up")
    @SendToUser("/queue/error/sign-up")
    public BasicPackage signUpUser(BasicPackage basicPackage) {
        if (basicPackage instanceof AddAccountCommand) {
            // Create account command
            AddAccountCommand addAccountCommand = (AddAccountCommand) basicPackage;
            if (addAccountCommand.getUsername() != null && !addAccountCommand.getUsername().isBlank() &&
                    addAccountCommand.getPassword() != null && !addAccountCommand.getPassword().isBlank() &&
                    !userAccountDatabaseAPI.checkUserExist(addAccountCommand.getUsername())) {

                // Create user in database
                userAccountDatabaseAPI.createUser(addAccountCommand.getUsername(),
                        encryptionAPI.asymmetricEncryptString(addAccountCommand.getPassword()), AccessLevels.ROLE_USER);

                // Create user in the broker
                messageBrokerAPI.createUser(addAccountCommand.getUsername());
                return new SignUpSuccessResponse(basicPackage.getMessageId());
            } else
                return new SignUpErrorResponse(basicPackage.getMessageId(), "Duplicated or invalid account");
        } else
            return new SignUpErrorResponse(basicPackage.getMessageId(), "Bad message format");
    }
}
