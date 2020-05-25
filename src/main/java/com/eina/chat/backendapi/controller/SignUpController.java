package com.eina.chat.backendapi.controller;

import com.eina.chat.backendapi.protocol.packages.*;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.signup.request.AddAccountCommand;
import com.eina.chat.backendapi.security.AccessLevels;
import com.eina.chat.backendapi.service.EncryptionAPI;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import com.eina.chat.backendapi.service.PersistentDataAPI;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class SignUpController {
    /**
     * Database API
     */
    @Autowired
    private PersistentDataAPI persistentDataAPI;

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


    private AtomicInteger users = Metrics.gauge("user", new AtomicInteger(0));
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
                    maxPasswordLength < addAccountCommand.getPassword().length() ||
                    !addAccountCommand.getUsername().matches("[a-zA-Z0-9]+")
            )
                return new OperationFailResponse(basicPackage.getMessageId(), "Username name must have between " +
                        minUsernameLength + " and " + maxUsernameLength + " characters (only alphanumeric allowed) and password " +
                        "must have between " + minPasswordLength + " and " + maxPasswordLength + " characters");

            else if (persistentDataAPI.checkUserExist(addAccountCommand.getUsername()))
                return new OperationFailResponse(basicPackage.getMessageId(), "Username already exist");

            else {

                // Create user in database
                persistentDataAPI.createUser(addAccountCommand.getUsername(),
                        encryptionAPI.asymmetricEncryptString(addAccountCommand.getPassword()), AccessLevels.ROLE_USER);

                // Create user in the broker
                messageBrokerAPI.createUser(addAccountCommand.getUsername());
                users.incrementAndGet();
                return new OperationSucceedResponse(basicPackage.getMessageId());
            }
        } else
            return new OperationFailResponse(basicPackage.getMessageId(), "Unknown command");
    }
}
