package com.eina.chat.backendapi.protocol.packages.message.request;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.SEARCH_USER)
public class SearchUserCommand extends BasicPackage {
    private String stringToSearch;

    @SuppressWarnings("unused")
    public SearchUserCommand() {
    }

    @SuppressWarnings("unused")
    public SearchUserCommand(int messageId, String stringToSearch) {
        super(messageId);
        this.stringToSearch = stringToSearch;
    }

    public String getStringToSearch() {
        return stringToSearch;
    }

    public void setStringToSearch(String stringToSearch) {
        this.stringToSearch = stringToSearch;
    }
}
