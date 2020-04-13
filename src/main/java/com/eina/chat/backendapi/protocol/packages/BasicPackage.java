package com.eina.chat.backendapi.protocol.packages;

import com.eina.chat.backendapi.protocol.packages.admin.request.SendMessageToAllCommand;
import com.eina.chat.backendapi.protocol.packages.message.request.*;
import com.eina.chat.backendapi.protocol.packages.message.response.FileFromRoomResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.FileFromUserResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.MessageFromRoomResponse;
import com.eina.chat.backendapi.protocol.packages.message.response.MessageFromUserResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.signup.request.AddAccountCommand;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "typeOfMessage"
)
@JsonSubTypes({
        // User request
        @JsonSubTypes.Type(value = AddAccountCommand.class, name = TypesOfMessage.ADD_ACCOUNT),
        @JsonSubTypes.Type(value = OperationSucceedResponse.class, name = TypesOfMessage.OPERATION_SUCCEED),
        @JsonSubTypes.Type(value = OperationFailResponse.class, name = TypesOfMessage.OPERATION_FAIL),
        @JsonSubTypes.Type(value = AddUserToChatRoomCommand.class, name = TypesOfMessage.ADD_USER_TO_CHAT_ROOM),
        @JsonSubTypes.Type(value = CreateRoomCommand.class, name = TypesOfMessage.CREATE_ROOM),
        @JsonSubTypes.Type(value = DeleteAccountCommand.class, name = TypesOfMessage.DELETE_ACCOUNT),
        @JsonSubTypes.Type(value = DeleteRoomCommand.class, name = TypesOfMessage.DELETE_ROOM),
        @JsonSubTypes.Type(value = RemoveUserFromChatRoom.class, name = TypesOfMessage.DELETE_USER_FROM_CHAT_ROOM),
        @JsonSubTypes.Type(value = SendFileToUserCommand.class, name = TypesOfMessage.SEND_FILE_TO_USER),
        @JsonSubTypes.Type(value = SendMessageToUserCommand.class, name = TypesOfMessage.SEND_MESSAGE_TO_USER),
        @JsonSubTypes.Type(value = SendFileToRoomCommand.class, name = TypesOfMessage.SEND_FILE_TO_ROOM),
        @JsonSubTypes.Type(value = SendMessageToRoomCommand.class, name = TypesOfMessage.SEND_MESSAGE_TO_ROOM),
        // User response
        @JsonSubTypes.Type(value = MessageFromUserResponse.class, name = TypesOfMessage.MESSAGE_FROM_USER),
        @JsonSubTypes.Type(value = FileFromUserResponse.class, name = TypesOfMessage.FILE_FROM_USER),
        @JsonSubTypes.Type(value = MessageFromRoomResponse.class, name = TypesOfMessage.MESSAGE_FROM_ROOM),
        @JsonSubTypes.Type(value = FileFromRoomResponse.class, name = TypesOfMessage.FILE_FROM_ROOM),
        // Admin request
        @JsonSubTypes.Type(value = SendMessageToAllCommand.class, name = TypesOfMessage.SEND_MESSAGE_TO_ALL),
        // Admin response
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
