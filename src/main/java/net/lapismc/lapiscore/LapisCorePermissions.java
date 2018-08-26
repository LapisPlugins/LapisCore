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

    private final HashMap<org.bukkit.permissions.Permission, HashMap<LapisPermission, Integer>> permissionSet = new HashMap<>();
    private final Cache<UUID, org.bukkit.permissions.Permission> playerPerms = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS).build();
    private LapisCorePlugin core;
    private PermissionManager permissionManager;

    public LapisCorePermissions(LapisCorePlugin core) {
        this.core = core;
        this.permissionManager = new PermissionManager();
        registerPermissions(new Default(), new Priority());
    }

    public void registerPermissions(LapisPermission... permission) {
        for (LapisPermission p : permission) {
            permissionManager.addPermission(p);
        }
    }

    public void loadPermissions() {
        permissionSet.clear();
        playerPerms.invalidateAll();
        ConfigurationSection permsSection = core.getConfig().getConfigurationSection("Permissions");
        Set<String> perms = permsSection.getKeys(false);
        for (String perm : perms) {
            HashMap<LapisPermission, Integer> permMap = new HashMap<>();
            String permName = perm.replace(",", ".");
            for (LapisPermission permission : permissionManager.getPermissions()) {
                int i = core.getConfig().getInt("Permissions." + perm + "." + permission.getName());
                permMap.put(permission, i);
            }
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
            Permission permission = new Permission(permName, permissionDefault);
            if (Bukkit.getPluginManager().getPermission(permName) == null) {
                Bukkit.getPluginManager().addPermission(permission);
            }
            permissionSet.put(permission, permMap);
        }
    }

    private Permission getPlayerPermission(UUID uuid) {
        Permission p = null;
        if (playerPerms.getIfPresent(uuid) != null) {
            return playerPerms.getIfPresent(uuid);
        }
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if (op.isOnline()) {
            Player player = op.getPlayer();
            Integer priority = -1;
            for (org.bukkit.permissions.Permission perm : permissionSet.keySet()) {
                if (player.hasPermission(perm) &&
                        (permissionSet.get(perm).get(permissionManager.getPermission("Priority")) > priority)) {
                    p = perm;
                    priority = permissionSet.get(perm).get(permissionManager.getPermission("Priority"));
                }
            }
            if (p == null) {
                return null;
            } else {
                playerPerms.put(uuid, p);
                savePlayersPermission(op, p);
            }
            return p;
        } else if (op.hasPlayedBefore()) {
            return getOfflinePlayerPermission(op);
        }
        return null;
    }

    public Boolean isPermitted(UUID uuid, LapisPermission perm) {
        HashMap<LapisPermission, Integer> permMap;
        Permission p = getPlayerPermission(uuid);
        if (!permissionSet.containsKey(p) || permissionSet.get(p) == null) {
            loadPermissions();
            permMap = permissionSet.get(p);
        } else {
            permMap = permissionSet.get(p);
        }
        return permMap != null && permMap.get(perm) != null && permMap.get(perm) >= 1;
    }

    public Integer getPermissionValue(UUID uuid, LapisPermission perm) {
        HashMap<LapisPermission, Integer> permMap;
        Permission p = getPlayerPermission(uuid);
        if (!permissionSet.containsKey(p) || permissionSet.get(p) == null) {
            loadPermissions();
            permMap = permissionSet.get(p);
        } else {
            permMap = permissionSet.get(p);
        }
        return permMap.get(perm);
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
