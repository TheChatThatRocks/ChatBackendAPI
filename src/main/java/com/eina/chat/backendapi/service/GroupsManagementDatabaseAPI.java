package com.eina.chat.backendapi.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupsManagementDatabaseAPI {
    /**
     * Create group if doesn't exist yet
     *  @param adminUsername admin username
     * @param groupName     group name
     */
    public void createGroup(@NonNull String adminUsername, @NonNull String groupName) {
        // TODO:
    }

    /**
     * Delete group
     *
     * @param groupName group name
     */
    public void deleteGroup(@NonNull String groupName) {
        // TODO:
    }

    /**
     * Add user to group
     *
     * @param username  user username
     * @param groupName group name
     */
    public void addUserToGroup(@NonNull String username, @NonNull String groupName) {
        // TODO:
    }

    /**
     * Remove user from group
     *
     * @param username  user username
     * @param groupName group name
     */
    public void removeUserFromGroup(@NonNull String username, @NonNull String groupName) {
        // TODO:
    }

    /**
     * Get the admin of a group
     *
     * @param groupName group name
     * @return admin username
     */
    public String getGroupAdmin(@NonNull String groupName) {
        // TODO:
        return "";
    }

    /**
     * Get members of a group
     *
     * @param groupName group name
     * @return members of the group
     */
    public List<String> getGroupMembers(@NonNull String groupName) {
        // TODO:
        return new ArrayList<>();
    }

    /**
     * Check if user is admin of the group
     *
     * @param username  user username
     * @param groupName group name
     * @return true if user is admin of the group
     */
    public boolean checkIfIsGroupAdmin(@NonNull String username, @NonNull String groupName) {
        // TODO:
        return true;
    }

    /**
     * Check is user is member of the group
     *
     * @param username  user username
     * @param groupName group name
     * @return true if is group member
     */
    public boolean checkIfIsGroupMember(@NonNull String username, @NonNull String groupName) {
        // TODO:
        return true;
    }

    /**
     * Remove user from all groups where user is member
     *
     * @param username user username
     */
    public void removeUserFromAllGroups(@NonNull String username) {
        // TODO:
    }

    /**
     * Delete all groups where user is admin
     *
     * @param username user username
     */
    public void deleteAllGroupsFromAdmin(@NonNull String username) {
        // TODO:
    }

    /**
     * Get all groups where user is member
     *
     * @param username user username
     * @return all groups where user is member
     */
    public List<String> getAllGroupsFromUser(@NonNull String username) {
        // TODO:
        return new ArrayList<>();
    }

    /**
     * Get all groups where user is admin
     *
     * @param username user username
     * @return all groups where user is member
     */
    public List<String> getAllGroupsWhereIsAdmin(@NonNull String username) {
        // TODO:
        return new ArrayList<>();
    }

    /**
     * Check if group exist
     *
     * @param groupName group name
     * @return true if group exist
     */
    public boolean checkIfGroupExist(@NonNull String groupName) {
        // TODO:
        return true;
    }
}
