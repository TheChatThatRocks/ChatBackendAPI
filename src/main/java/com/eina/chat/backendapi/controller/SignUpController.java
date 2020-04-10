package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.signup.request.AddAccountCommand;
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
            if (addAccountCommand.getUsername() != null && !addAccountCommand.getUsername().isBlank() && addAccountCommand.getUsername().length() <= 50 &&
                    addAccountCommand.getPassword() != null && 8 <= addAccountCommand.getPassword().length() && addAccountCommand.getPassword().length() <= 50 &&
                    !userAccountDatabaseAPI.checkUserExist(addAccountCommand.getUsername())) {

                // Create user in database
                userAccountDatabaseAPI.createUser(addAccountCommand.getUsername(),
                        encryptionAPI.asymmetricEncryptString(addAccountCommand.getPassword()), AccessLevels.ROLE_USER);

                // Create user in the broker
                messageBrokerAPI.createUser(addAccountCommand.getUsername());
                return new OperationSucceedResponse(basicPackage.getMessageId());
            } else
                return new OperationFailResponse(basicPackage.getMessageId(), TypesOfMessage.SIGN_UP_ERROR, "Duplicated or invalid account");
        } else
            return new OperationFailResponse(basicPackage.getMessageId(), TypesOfMessage.SIGN_UP_ERROR, "Bad message format");
    }
}
