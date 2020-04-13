package com.eina.chat.backendapi.protocol.packages.admin.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.STATS)
public class StatsResponse {

    public class FileFromRoomResponse extends BasicPackage {

        @SuppressWarnings("unused")
        public FileFromRoomResponse() {
            super();
        }
    }
}
