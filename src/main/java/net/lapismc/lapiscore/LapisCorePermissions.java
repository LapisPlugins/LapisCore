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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LapisCorePermissions {

    private final ArrayList<PlayerPermission> permissionSet = new ArrayList<>();
    private final Cache<UUID, Permission> playerPerms = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS).build();
    private LapisCorePlugin core;
    private PermissionManager permissionManager;

    public LapisCorePermissions(LapisCorePlugin core) {
        this.core = core;
        this.permissionManager = new PermissionManager();
        registerPermissions(new Default(), new Priority());
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
     * Get the PlayerPermission for a given Bukkit permission
     *
     * @param perm The Bukkit permission you wish to look up
     * @return Returns the PlayerPermission associated with the given Bukkit permission
     */
    private PlayerPermission getPlayerPermission(Permission perm) {
        for (PlayerPermission perms : permissionSet) {
            if (perms.getPermission().equals(perm)) {
                return perms;
            }
        }
        return null;
    }

    /**
     * Force the plugin to load permission values from the config
     */
    public void loadPermissions() {
        permissionSet.clear();
        playerPerms.invalidateAll();
        //Get the permissions section of the config
        ConfigurationSection permsSection = core.getConfig().getConfigurationSection("Permissions");
        Set<String> perms = permsSection.getKeys(false);
        //Loop through each permission
        for (String perm : perms) {
            HashMap<LapisPermission, Integer> permMap = new HashMap<>();
            //Get the name that will be registered with Bukkit
            String permName = perm.replace(",", ".");
            //Loop over each permission that has been registered by the plugin
            for (LapisPermission permission : permissionManager.getPermissions()) {
                //For each, get the integer stored and add it to the map
                int i = core.getConfig().getInt("Permissions." + perm + "." + permission.getName(), 0);
                permMap.put(permission, i);
            }
            //Check the Default permission value and store it for when we make the permission
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
            //Attempt to get the permission, if it doesn't exist we create and register it
            Permission permission = Bukkit.getPluginManager().getPermission(permName);
            if (permission == null) {
                permission = new Permission(permName, permissionDefault);
                Bukkit.getPluginManager().addPermission(permission);
            }
            //Add it to the permissions set
            permissionSet.add(new PlayerPermission(permission, permMap));
        }
    }

    /**
     * Get the permission assigned to a player with the highest priority
     *
     * @param uuid The UUID of the player
     * @return Returns the Bukkit Permission that should apply to the given UUID
     */
    private Permission getPlayerPermission(UUID uuid) {
        Permission p = null;
        //Return the players stored permission if one is stored
        if (playerPerms.getIfPresent(uuid) != null) {
            return playerPerms.getIfPresent(uuid);
        }
        //If one isn't stored we have to find it the hard way
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        //If the player is online we can check their permissions
        //and see if they have any of ours and which has the higher priority
        if (op.isOnline()) {
            Player player = op.getPlayer();
            Integer priority = -1;
            //Loop over our permissions and check the priorities to find the permission with the highest priority
            for (PlayerPermission playerPermission : permissionSet) {
                Permission perm = playerPermission.getPermission();
                if (player.hasPermission(perm) &&
                        (playerPermission.getPermissions().get(permissionManager.getPermission("Priority")) > priority)) {
                    p = perm;
                    priority = playerPermission.getPermissions().get(permissionManager.getPermission("Priority"));
                }
            }
            if (p == null) {
                //If we don't find a permission of ours we return null
                return null;
            } else {
                //If we do find a permission we add it to the cache and save it if that method has been implemented
                playerPerms.put(uuid, p);
                savePlayersPermission(op, p);
            }
            return p;
        } else if (op.hasPlayedBefore()) {
            //If the player isn't online we will attempt to get the permission if the plugin has implemented that feature
            return getOfflinePlayerPermission(op);
        }
        return null;
    }

    /**
     * Check if the player has a value of 1 or greater for the given LapisPermission
     *
     * @param uuid The UUID of a player
     * @param perm The LapisPermission you wish to check
     * @return Returns true if the players permission has a value of 1 or higher for the given LapisPermission
     */
    public Boolean isPermitted(UUID uuid, LapisPermission perm) {
        Permission p = getPlayerPermission(uuid);
        PlayerPermission permission = getPlayerPermission(p);
        if (permission == null) {
            loadPermissions();
            permission = getPlayerPermission(p);
        }
        //noinspection ConstantConditions
        return permission.getPermissions().get(perm) >= 1;
    }

    /**
     * Get the raw value for the given LapisPermission and UUID
     *
     * @param uuid The UUID of the player
     * @param perm The LapisPermission you want the value of
     * @return Returns the raw Integer value for the given LapisPermission and Player
     */
    public Integer getPermissionValue(UUID uuid, LapisPermission perm) {
        Permission p = getPlayerPermission(uuid);
        PlayerPermission permission = getPlayerPermission(p);
        if (permission == null) {
            loadPermissions();
            permission = getPlayerPermission(p);
        }
        //noinspection ConstantConditions
        return permission.getPermissions().get(perm);
    }

    protected Permission getOfflinePlayerPermission(OfflinePlayer op) {
        return null;
    }

    protected void savePlayersPermission(OfflinePlayer op, Permission perm) {

    }

    public class PermissionManager {
        private ArrayList<LapisPermission> permissions = new ArrayList<>();

        protected void addPermission(LapisPermission permission) {
            permissions.add(permission);
        }

        protected List<LapisPermission> getPermissions() {
            return permissions;
        }

        public LapisPermission getPermission(String name) {
            for (LapisPermission permission : permissions) {
                if (permission.getName().equalsIgnoreCase(name)) {
                    return permission;
                }
            }
            return null;
        }
    }

    private class Priority extends LapisPermission {
        Priority() {
            super("Priority");
        }
    }

    private class Default extends LapisPermission {
        Default() {
            super("Default");
        }
    }
}
