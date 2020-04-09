package com.eina.chat.backendapi.protocol.packages;

import com.eina.chat.backendapi.protocol.packages.message.request.SendMessageToUserCommand;
import com.eina.chat.backendapi.protocol.packages.message.response.MessageFromUserResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.SendMessageToUserErrorResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.UnknownCommandResponse;
import com.eina.chat.backendapi.protocol.packages.signup.request.AddAccountCommand;
import com.eina.chat.backendapi.protocol.packages.signup.response.SignUpErrorResponse;
import com.eina.chat.backendapi.protocol.packages.signup.response.SignUpSuccessResponse;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "typeOfMessage"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddAccountCommand.class, name = TypesOfMessage.ADD_ACCOUNT),
        @JsonSubTypes.Type(value = SignUpSuccessResponse.class, name = TypesOfMessage.SIGN_UP_SUCCESS),
        @JsonSubTypes.Type(value = SignUpErrorResponse.class, name = TypesOfMessage.SIGN_UP_ERROR),
        @JsonSubTypes.Type(value = SendMessageToUserCommand.class, name = TypesOfMessage.SEND_MESSAGE_TO_USER),
        @JsonSubTypes.Type(value = MessageFromUserResponse.class, name = TypesOfMessage.MESSAGE_FROM_USER),
        @JsonSubTypes.Type(value = OperationSucceedResponse.class, name = TypesOfMessage.OPERATION_SUCCEED),
        @JsonSubTypes.Type(value = UnknownCommandResponse.class, name = TypesOfMessage.UNKNOWN_COMMAND),
        @JsonSubTypes.Type(value = SendMessageToUserErrorResponse.class, name = TypesOfMessage.SEND_MESSAGE_TO_USER_ERROR)
})
public abstract class BasicPackage {
    private int messageId;

    @SuppressWarnings("unused")
    public BasicPackage() {
    }

    @SuppressWarnings("unused")
    public BasicPackage(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

}
