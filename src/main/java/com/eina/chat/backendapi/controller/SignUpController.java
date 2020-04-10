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
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Min and max username length
     */
    @Value("${app.max-username-length:}")
    private Integer maxUsernameLength;

    @Value("${app.min-username-length:}")
    private Integer minUsernameLength;

    /**
     * Min and max password length
     */
    @Value("${app.max-password-length:}")
    private Integer maxPasswordLength;

    @Value("${app.min-password-length:}")
    private Integer minPasswordLength;

    @MessageMapping("/sign-up")
    @SendToUser("/queue/error/sign-up")
    public BasicPackage signUpUser(BasicPackage basicPackage) {
        if (basicPackage instanceof AddAccountCommand) {
            // Create account command
            AddAccountCommand addAccountCommand = (AddAccountCommand) basicPackage;
            if (addAccountCommand.getUsername() == null || addAccountCommand.getPassword() == null ||
                    addAccountCommand.getUsername().length() < minUsernameLength ||
                    maxUsernameLength < addAccountCommand.getUsername().length() ||
                    addAccountCommand.getPassword().length() < minPasswordLength ||
                    maxPasswordLength < addAccountCommand.getPassword().length()
            )
                return new OperationFailResponse(basicPackage.getMessageId(), "Username name must have between " +
                        minUsernameLength + " and " + maxUsernameLength + " characters and password " +
                        "must have between " + minPasswordLength + " and " + maxPasswordLength + " characters");

            else if (userAccountDatabaseAPI.checkUserExist(addAccountCommand.getUsername()))
                return new OperationFailResponse(basicPackage.getMessageId(), "Username already exist");

            else {

                // Create user in database
                userAccountDatabaseAPI.createUser(addAccountCommand.getUsername(),
                        encryptionAPI.asymmetricEncryptString(addAccountCommand.getPassword()), AccessLevels.ROLE_USER);

                // Create user in the broker
                messageBrokerAPI.createUser(addAccountCommand.getUsername());
                return new OperationSucceedResponse(basicPackage.getMessageId());
            }
        } else
            return new OperationFailResponse(basicPackage.getMessageId(), "Unknown command");
    }
}
