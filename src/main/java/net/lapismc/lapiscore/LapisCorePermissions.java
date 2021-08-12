/*
 * Copyright 2021 Benjamin Martin
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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.lapismc.lapiscore.permissions.LapisPermission;
import net.lapismc.lapiscore.permissions.PlayerPermission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the complex custom permission system that LapisCore
 */
public class LapisCorePermissions {

    private final LapisCorePlugin core;
    private final PermissionManager permissionManager;
    private final ArrayList<PlayerPermission> permissions = new ArrayList<>();
    private final Cache<UUID, PlayerPermission> assignedPermissionCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS).build();

    /**
     * @param core The LapisCore class that the permissions should be assigned too
     */
    public LapisCorePermissions(LapisCorePlugin core) {
        this.core = core;
        permissionManager = new PermissionManager();
        registerPermissions(new Priority(), new Default());
    }

    /**
     * Register permissions with LapisPermission objects
     *
     * @param permission The permissions you wish to add
     */
    public void registerPermissions(LapisPermission... permission) {
        for (LapisPermission p : permission) {
            permissionManager.addPermission(p);
        }
    }

    /**
     * Call this method to load the permission values from config
     * once all of your LapisPermissions have been registered
     */
    public void loadPermissions() {
        //clear the list before its populated again in case this is a permission reload
        permissions.clear();
        //get the permissions section of the config
        ConfigurationSection permsSection = core.getConfig().getConfigurationSection("Permissions");
        Set<String> perms = permsSection.getKeys(false);
        //loop through each permission
        for (String configPermission : perms) {
            //get its actual name
            String permName = configPermission.replace(",", ".");
            //make a map to store the values
            Map<LapisPermission, Integer> permMap = new HashMap<>();
            //loop through all registered values
            for (LapisPermission value : permissionManager.getPermissions()) {
                //get the config path for the value
                String path = "Permissions." + configPermission + "." + value.getName();
                //load it from the config with the default from the plugin
                permMap.put(value, core.getConfig().getInt(path, value.getDefaultValue()));
            }
            //If the permission doesnt exist we want to register it
            if (Bukkit.getPluginManager().getPermission(permName) == null) {
                //get the permission default so that it is registered correctly
                PermissionDefault permissionDefault;
                switch (permMap.get(permissionManager.getPermission("Default"))) {
                    case 1:
                        permissionDefault = PermissionDefault.TRUE;
                        break;
                    case 2:
                        permissionDefault = PermissionDefault.OP;
                        break;
                    case 0:
                    default:
                        permissionDefault = PermissionDefault.FALSE;
                        break;
                }
                //register the permission
                Bukkit.getPluginManager().addPermission(new Permission(permName, permissionDefault));
            }
            //get the registered Bukkit permission
            Permission bukkitPermission = Bukkit.getPluginManager().getPermission(permName);
            //make a player permission to store the permission map in
            PlayerPermission permission = new PlayerPermission(bukkitPermission, permMap);
            //add the permission to the ArrayList
            permissions.add(permission);
        }
    }

    /**
     * Get the Bukkit permission assigned to a player
     *
     * @param uuid The UUID of the player
     * @return Returns the Bukkit Permission that the plugin is using for permission calculations
     */
    public Permission getAssignedPermission(UUID uuid) {
        if (calculatePermission(uuid) != null)
            return calculatePermission(uuid).getPermission();
        else
            return null;
    }

    /**
     * Get the raw Integer value of the permission for a player
     *
     * @param uuid       The UUID of the player
     * @param permission The Permission you want a value for
     * @return Returns the Integer value for the permission assigned to the player,
     * returns 0 if a permission could not be calculated
     */
    public Integer getPermissionValue(UUID uuid, LapisPermission permission) {
        PlayerPermission playerPerm = calculatePermission(uuid);
        //If its null its safest to send a 0
        if (playerPerm == null)
            return 0;
        return playerPerm.getPermissionValue(permission);
    }

    /**
     * Check if a player is given a permission
     *
     * @param uuid       The UUID of the player
     * @param permission The Permission you wish to check
     * @return Returns true if the value of Permission is greater than 0 otherwise false
     */
    public boolean isPermitted(UUID uuid, LapisPermission permission) {
        Integer value = getPermissionValue(uuid, permission);
        return getPermissionValue(uuid, permission) > 0;
    }

    /**
     * Calculate the PlayerPermission for a player
     *
     * @param uuid The UUID of the player
     * @return Returns the PlayerPermission assigned to the player
     */
    public PlayerPermission calculatePermission(UUID uuid) {
        //first check the cache
        PlayerPermission cachedValue = assignedPermissionCache.getIfPresent(uuid);
        if (cachedValue != null) {
            return cachedValue;
        }
        //if its not in the cache then we much calculate it
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            Player p = Bukkit.getPlayer(uuid);
            //create a list of the permissions the player has
            List<PlayerPermission> candidatePermissions = new ArrayList<>();
            for (PlayerPermission toTest : permissions) {
                if (p.hasPermission(toTest.getPermission())) {
                    candidatePermissions.add(toTest);
                }
            }
            //find the permission with the highest priority out of the candidates
            PlayerPermission assignedPermission = null;
            LapisPermission priority = permissionManager.getPermission("Priority");
            for (PlayerPermission candidatePerm : candidatePermissions) {
                //make the candidate the assigned permission if there is no
                //assigned permission or the candidate has a higher priority
                if (assignedPermission == null ||
                        assignedPermission.getPermissionValue(priority) < candidatePerm.getPermissionValue(priority)) {
                    assignedPermission = candidatePerm;
                }
            }
            //put the assigned permission in the cache and send it to plugins before returning it
            //only add it to the cache if its not null
            if (assignedPermission != null) {
                savePlayersPermission(uuid, assignedPermission.getPermission());
                assignedPermissionCache.put(uuid, assignedPermission);
            }
            return assignedPermission;
        } else {
            //get the permission from the plugin should it be implemented
            PlayerPermission assignedPermission = convertPermission(
                    getOfflinePlayerPermission(uuid));
            //only add it to the cache if its not null
            if (assignedPermission != null)
                assignedPermissionCache.put(uuid, assignedPermission);
            return assignedPermission;
        }
    }

    /**
     * Converts a Bukkit permission to a PlayerPermission
     *
     * @param perm The Bukkit permission you wish to convert
     * @return Returns the appropriate PlayerPermission, null if none match
     */
    private PlayerPermission convertPermission(Permission perm) {
        for (PlayerPermission candidate : permissions) {
            if (candidate.getPermission() == perm)
                return candidate;
        }
        return null;
    }

    /**
     * Override this method to provide a method of retrieving the players permission while they are offline
     *
     * @param uuid The UUID of the player we want the permission of
     * @return Return the Bukkit Permission that is assigned to this player
     */
    protected Permission getOfflinePlayerPermission(UUID uuid) {
        return null;
    }

    /**
     * Override this method to save the players permission for retrieval when they are offline
     *
     * @param uuid The UUID of the player the permission is assigned to
     * @param perm The permission the player has been assigned
     */
    protected void savePlayersPermission(UUID uuid, Permission perm) {

    }

    /**
     * This class is used to store and retrieve permissions
     */
    public static class PermissionManager {

        private final ArrayList<LapisPermission> permissions = new ArrayList<>();

        /**
         * Adds the given permission to the stored permissions for later use
         *
         * @param permission The permission to be added
         */
        protected void addPermission(LapisPermission permission) {
            permissions.add(permission);
        }

        /**
         * Get all registered permissions
         *
         * @return A list of all registered LapisPermissions
         */
        protected List<LapisPermission> getPermissions() {
            return permissions;
        }

        /**
         * Retrieve a specific permission given its name
         *
         * @param name The name of the permission you wish to receive
         * @return the LapisPermission with the given name, null if there is no permission with this name
         */
        public LapisPermission getPermission(String name) {
            for (LapisPermission permission : permissions) {
                if (permission.getName().equalsIgnoreCase(name)) {
                    return permission;
                }
            }
            return null;
        }
    }

    private static class Priority extends LapisPermission {
        Priority() {
            super("Priority", 0);
        }
    }

    private static class Default extends LapisPermission {
        Default() {
            super("Default", 0);
        }
    }
}
