package com.eina.chat.backendapi.data.documental.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "group_messages")
public class GroupMessageVo {

    private String username;
    private String groupName;
    private String content;

    public GroupMessageVo() {
    }

    public GroupMessageVo(String username, String groupName, String content) {
        this.username = username;
        this.groupName = groupName;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
