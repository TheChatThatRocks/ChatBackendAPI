package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.JOINED_ROOMS_CHAT_HISTORY)
public class JoinedRoomsChatHistoryCommand extends BasicPackage {
    @SuppressWarnings("unused")
    public JoinedRoomsChatHistoryCommand() {
    }

    @SuppressWarnings("unused")
    public JoinedRoomsChatHistoryCommand(int messageId) {
        super(messageId);
    }

}
