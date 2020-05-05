package com.eina.chat.backendapi.protocol.packages.admin.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.CHECK_STATS)
public class CheckStatsCommand extends BasicPackage {

    @SuppressWarnings("unused")
    public CheckStatsCommand() {
        super();
    }
}
