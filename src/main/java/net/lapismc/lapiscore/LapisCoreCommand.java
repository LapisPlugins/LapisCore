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

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * An utility class to make custom commands that are not in the plugin.yml
 */
public abstract class LapisCoreCommand extends BukkitCommand {

    private final LapisCorePlugin core;

    /**
     * If in doubt use this constructor
     *
     * @param core    The {@link LapisCorePlugin} that the command should be registered to
     * @param name    The name of the command, this wont include the slash
     * @param desc    The description for the /help menu
     * @param aliases Any aliases that should run this command
     */
    protected LapisCoreCommand(LapisCorePlugin core, String name, String desc, ArrayList<String> aliases) {
        this(core, name, desc, aliases, false);
    }

    /**
     * This constructor allows you to take conflicting commands and aliases
     *
     * @param core          The {@link LapisCorePlugin} that the command should be registered to
     * @param name          The name of the command, this wont include the slash
     * @param desc          The description for the /help menu
     * @param aliases       Any aliases that should run this command
     * @param takeConflicts Set to true if you would like to forcefully take control of any commands of the same name or alias
     */
    protected LapisCoreCommand(LapisCorePlugin core, String name, String desc, ArrayList<String> aliases, boolean takeConflicts) {
        super(name);
        this.core = core;
        setDescription(desc);
        setAliases(aliases);
        setupCommand(name, takeConflicts);
    }

    private void setupCommand(String name, boolean takeConflicts) {
        if (takeConflicts) {
            Bukkit.getScheduler().runTask(core, this::takeConflictingAliases);
        }
        registerCommand(name);
    }

    private void registerCommand(String name) {
        try {
            final Field serverCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            serverCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) serverCommandMap.get(Bukkit.getServer());
            commandMap.register(name, this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void takeConflictingAliases() {
        for (String alias : getAliases()) {
            if (Bukkit.getPluginCommand(alias) != null) {
                PluginCommand command = Bukkit.getPluginCommand(alias);
                command.setExecutor(new LapisCoreCommandExecutor());
            }
        }
        if (Bukkit.getPluginCommand(getName()) != null) {
            PluginCommand command = Bukkit.getPluginCommand(getName());
            if (!command.getPlugin().equals(core)) {
                command.setExecutor(new LapisCoreCommandExecutor());
            }
        }
    }

    /**
     * Check if a sender is permitted, requires {@link LapisCorePermissions} to be registered in {@link LapisCorePlugin}
     *
     * @param sender     The sender of a command, player or console
     * @param permission The LapisPermission you wish to check
     * @return Returns true if the value of the given permission is greater than 0 or if the sender is not a {@link Player}
     */
    protected boolean isPermitted(CommandSender sender, LapisPermission permission) {
        if (sender instanceof Player) {
            if (core.perms != null) {
                return core.perms.isPermitted(((Player) sender).getUniqueId(), permission);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Send a message from the messages.yml to the command sender provided,
     * requires {@link LapisCoreConfiguration} to be registered in {@link LapisCorePlugin}
     *
     * @param sender The command sender who you wish to send a message to
     * @param key    The key for the message in the messages.yml
     */
    protected void sendMessage(CommandSender sender, String key) {
        sender.sendMessage(core.config.getMessage(key));
    }

    protected boolean isNotPlayer(CommandSender sender, String key) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, key);
            return true;
        }
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        onCommand(sender, commandLabel, args);
        return true;
    }

    protected void onCommand(CommandSender sender, String commandLabel, String[] args) {
        onCommand(sender, args);
    }

    protected abstract void onCommand(CommandSender sender, String[] args);

    private class LapisCoreCommandExecutor implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            return execute(sender, label, args);
        }

    }

}
