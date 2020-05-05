package com.eina.chat.backendapi.data.relational.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GroupVo {
    @Id
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
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

    public void setMembers(List<UserVo> members) {
        this.members = members;
    }

    public void addMember(UserVo member) {
        this.members.add(member);
    }

    public void removeMember(UserVo member) {
        this.members.remove(member);
    }
}