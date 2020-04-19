package com.eina.chat.backendapi.data.documental.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "group_files")
public class GroupFileVo {

    private String username;
    private String groupName;
    private byte[] content;

    public GroupFileVo() {
    }

    public GroupFileVo(String username, String groupName, byte[] content) {
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
