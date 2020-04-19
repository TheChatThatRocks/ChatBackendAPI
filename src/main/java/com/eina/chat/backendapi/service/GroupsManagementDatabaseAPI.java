package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.data.relational.dao.GroupDao;
import com.eina.chat.backendapi.data.relational.model.GroupVo;
import com.eina.chat.backendapi.data.relational.dao.UserDao;
import com.eina.chat.backendapi.data.relational.model.UserVo;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupsManagementDatabaseAPI {
    /**
     * Group database
     */
    @Autowired
    private GroupDao groupDao;

    /**
     * User database
     */
    @Autowired
    private UserDao userDao;


    /**
     * Create group if doesn't exist yet
     *
     * @param adminUsername admin username
     * @param groupName     group name
     */
    public void createGroup(@NonNull String adminUsername, @NonNull String groupName) {
        userDao.findById(adminUsername).ifPresent(userVo -> groupDao.save(new GroupVo(groupName, userVo)));
    }

    /**
     * Delete group
     *
     * @param groupName group name
     */
    public void deleteGroup(@NonNull String groupName) {
        if (groupDao.existsById(groupName))
            groupDao.deleteById(groupName);
    }

    /**
     * Add user to group
     *
     * @param username  user username
     * @param groupName group name
     */
    public void addUserToGroup(@NonNull String username, @NonNull String groupName) {
        Optional<UserVo> userVoOptional = userDao.findById(username);
        Optional<GroupVo> groupVoOptional = groupDao.findById(groupName);
        if (userVoOptional.isPresent() && groupVoOptional.isPresent()) {
            GroupVo groupVo = groupVoOptional.get();
            groupVo.getMembers().add(userVoOptional.get());
            groupDao.save(groupVo);
        }
    }

    /**
     * Remove user from group
     *
     * @param username  user username
     * @param groupName group name
     */
    public void removeUserFromGroup(@NonNull String username, @NonNull String groupName) {
        Optional<GroupVo> groupVoOptional = groupDao.findById(groupName);
        if (groupVoOptional.isPresent()) {
            GroupVo groupVo = groupVoOptional.get();
            groupVo.setMembers(groupVo.getMembers().stream().filter(userVo -> !userVo.getUsername().equals(username))
                    .collect(Collectors.toList()));
            groupDao.save(groupVo);
        }
    }

    /**
     * Get the admin of a group
     *
     * @param groupName group name
     * @return admin username
     */
    public String getGroupAdmin(@NonNull String groupName) {
        return groupDao.findById(groupName).map(groupVo -> groupVo.getAdmin().getUsername()).orElse(null);
    }

    /**
     * Get members of a group
     *
     * @param groupName group name
     * @return members of the group
     */
    public List<String> getGroupMembers(@NonNull String groupName) {
        List<String> groupMembers = groupDao.findById(groupName).map(groupVo -> groupVo.getMembers().stream().map(UserVo::getUsername)
                .collect(Collectors.toList())).orElse(new ArrayList<>());
        groupMembers.add(this.getGroupAdmin(groupName));
        return groupMembers;
    }

    /**
     * Check if user is admin of the group
     *
     * @param username  user username
     * @param groupName group name
     * @return true if user is admin of the group
     */
    public boolean checkIfIsGroupAdmin(@NonNull String username, @NonNull String groupName) {
        return groupDao.findById(groupName).map(groupVo -> groupVo.getAdmin().getUsername().equals(username)).orElse(false);
    }

    /**
     * Check is user is member of the group
     *
     * @param username  user username
     * @param groupName group name
     * @return true if is group member
     */
    public boolean checkIfIsGroupMember(@NonNull String username, @NonNull String groupName) {
        return groupDao.findById(groupName).map(groupVo -> groupVo.getMembers().stream()
                .anyMatch(userVo -> userVo.getUsername().equals(username))).orElse(false)
                || this.checkIfIsGroupAdmin(username, groupName);
    }

    /**
     * Get all groups where user is member
     *
     * @param username user username
     * @return all groups where user is member (including where is admin)
     */
    public List<String> getAllGroupsWhereIsMember(@NonNull String username) {
        List<String> groupsWhereIsMember = userDao.findById(username).map(vo -> vo.getGroups().stream()
                .map(GroupVo::getName).collect(Collectors.toList())).orElseGet(ArrayList::new);
        groupsWhereIsMember.addAll(this.getAllGroupsWhereIsAdmin(username));
        return groupsWhereIsMember;
    }

    /**
     * Get all groups where user is admin
     *
     * @param username user username
     * @return all groups where user is member
     */
    public List<String> getAllGroupsWhereIsAdmin(@NonNull String username) {
        Optional<UserVo> userVoOptional = userDao.findById(username);
        return userVoOptional.map(userVo -> groupDao.findByAdmin(userVo).stream()
                .map(GroupVo::getName).collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    /**
     * Check if group exist
     *
     * @param groupName group name
     * @return true if group exist
     */
    public boolean checkIfGroupExist(@NonNull String groupName) {
        return groupDao.existsById(groupName);
    }
}
