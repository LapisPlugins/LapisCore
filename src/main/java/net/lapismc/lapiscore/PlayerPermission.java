/*
 * Copyright 2018 Benjamin Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lapismc.lapiscore;

import org.bukkit.permissions.Permission;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to represent the permissions given to players and the values applied to them
 */
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
