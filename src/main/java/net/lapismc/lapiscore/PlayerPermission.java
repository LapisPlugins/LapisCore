package net.lapismc.lapiscore;

import org.bukkit.permissions.Permission;

import java.util.HashMap;
import java.util.Map;

public class PlayerPermission {

    private Permission permission;
    private Map<LapisPermission, Integer> permissions;

    /**
     * @param permission  The Bukkit permission a player must have to be given this set of LapisPermission values
     * @param permissions The Map of LapisPermissions and their values for this Player Permission
     */
    public PlayerPermission(Permission permission, Map<LapisPermission, Integer> permissions) {
        this.permission = permission;
        this.permissions = permissions;
    }

    /**
     * @return Returns the map of LapisPermissions and values for this PlayerPermission
     */
    public Map<LapisPermission, Integer> getPermissions() {
        if (permissions != null)
            return permissions;
        else
            return new HashMap<>();
    }

    /**
     * @param lapisPermission The permission value to return
     * @return The value of the given LapisPermission for this player permission
     */
    public Integer getPermissionValue(LapisPermission lapisPermission) {
        return permissions.get(lapisPermission);
    }

    /**
     * @return Returns the Bukkit Permission associated with this set of LapisPermissions
     */
    public Permission getPermission() {
        return permission;
    }
}
