package com.eina.chat.backendapi.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GroupVo {
    @Id
    private String name;

    // TODO: Check this optional parameters
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserVo admin;

    // TODO: Check this optional parameters
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UserVo> members = new ArrayList<>();

    public GroupVo() {
    }

    public GroupVo(String name, UserVo admin) {
        this.name = name;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserVo getAdmin() {
        return admin;
    }

    public void setAdmin(UserVo admin) {
        this.admin = admin;
    }

    public List<UserVo> getMembers() {
        return members;
    }

    public void addMember(UserVo member) {
        this.members.add(member);
    }

    public void removeMember(UserVo member) {
        this.members.remove(member);
    }
}