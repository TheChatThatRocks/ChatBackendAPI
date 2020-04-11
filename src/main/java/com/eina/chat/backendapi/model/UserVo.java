package com.eina.chat.backendapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserVo {
    @Id
    private String username;

    private String password;

    private String role;

    @ManyToMany(mappedBy = "members")
    private List<GroupVo> groups = new ArrayList<>();

    public UserVo() {
    }

    public UserVo(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public List<GroupVo> getGroups() {
        return groups;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void addGroup(GroupVo groupVo) {
        groups.add(groupVo);
    }

    public void removeGroup(GroupVo groupVo) {
        groups.remove(groupVo);
    }
}
