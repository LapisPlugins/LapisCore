/*
 * Copyright 2020 Benjamin Martin
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

import net.lapismc.lapiscore.permissions.LapisPermission;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

/**
 * An utility class to make custom commands that are not in the plugin.yml
 */
public abstract class LapisCoreCommand extends BukkitCommand {

    private final LapisCorePlugin core;
    private TabCompleter tabCompleter;

    /**
     * If in doubt use this constructor
     *
     * @param core    The {@link LapisCorePlugin} that the command should be registered to
     * @param name    The name of the command, this wont include the slash
     * @param desc    The description for the /help menu
     * @param aliases Any aliases that should run this command
     */
    protected LapisCoreCommand(LapisCorePlugin core, String name, String desc, List<String> aliases) {
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
    protected LapisCoreCommand(LapisCorePlugin core, String name, String desc, List<String> aliases, boolean takeConflicts) {
        super(name);
        this.core = core;
        setDescription(desc);
        setAliases(aliases);
        setupCommand(takeConflicts);
    }

    private void setupCommand(boolean takeConflicts) {
        registerCommand();
        if (takeConflicts) {
            Bukkit.getScheduler().runTask(core, this::takeConflictingAliases);
        }
    }

    /**
     * Registers the command in the servers command map
     */
    private void registerCommand() {
        try {
            final Field serverCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            serverCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) serverCommandMap.get(Bukkit.getServer());
            commandMap.register(getName(), this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to redirect conflicting commands or aliases to this command
     */
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
     * Registers the given class as the tab completer for this command
     *
     * @param completer The class you wish to deal with tab completions for this command
     */
    protected void registerTabCompleter(TabCompleter completer) {
        tabCompleter = completer;
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

    /**
     * Test if the sender is a player and send them a message if they are not a player
     *
     * @param sender The command sender you wish to test for
     * @param key    The key of the message they should receive if they are not a player
     * @return Returns true if the sender is not a player, otherwise returns false
     */
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

    /**
     * Calls the default behaviour unless a tab completer has been set with {@link #registerTabCompleter(TabCompleter)},
     * from 1.13 onwards this is constantly triggered for possible completions displayed above the chat bar
     *
     * @param sender The sender who has attempted to tab complete
     * @param alias  Alias of the command that they are using
     * @param args   The current arguments of the command
     * @return Returns a list of possible completions for the current arguments
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (tabCompleter == null) {
            return super.tabComplete(sender, alias, args);
        } else {
            return tabCompleter.onTabComplete(sender, this, alias, args);
        }
    }

    private class LapisCoreCommandExecutor implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            return execute(sender, label, args);
        }

    }

}
