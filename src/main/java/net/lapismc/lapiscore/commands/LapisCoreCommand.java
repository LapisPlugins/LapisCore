/*
 * Copyright 2025 Benjamin Martin
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

package net.lapismc.lapiscore.commands;

import net.lapismc.lapiscore.LapisCoreConfiguration;
import net.lapismc.lapiscore.LapisCorePermissions;
import net.lapismc.lapiscore.LapisCorePlugin;
import net.lapismc.lapiscore.permissions.LapisPermission;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * An utility class to make custom commands that are not in the plugin.yml
 */
public abstract class LapisCoreCommand extends BukkitCommand {

    private final LapisCorePlugin core;
    private TabCompleter tabCompleter;
    private final List<String> takenAliases;

    /**
     * If in doubt use this constructor
     *
     * @param core    The {@link LapisCorePlugin} that the command should be registered to
     * @param name    The name of the command, this won't include the slash
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
     * @param name          The name of the command, this won't include the slash
     * @param desc          The description for the /help menu
     * @param aliases       Any aliases that should run this command
     * @param takeConflicts Set to true if you would like to forcefully take control of any commands of the same name or alias
     */
    protected LapisCoreCommand(LapisCorePlugin core, String name, String desc, List<String> aliases, boolean takeConflicts) {
        super(name);
        this.core = core;
        takenAliases = new ArrayList<>();
        setDescription(desc);
        setAliases(aliases);
        setupCommand(takeConflicts);
        CommandRegistry.registerCommand(this);
    }

    /**
     * Get a list of aliases that this command has attempted to override from other plugins
     *
     * @return A list of command names as strings
     */
    public List<String> getTakenAliases() {
        return takenAliases;
    }

    private void setupCommand(boolean takeConflicts) {
        registerCommand();
        if (takeConflicts) {
            core.tasks.runTask(this::takeConflictingCommands, false);
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
    private void takeConflictingCommands() {
        for (String alias : getAliases()) {
            if (Bukkit.getPluginCommand(alias) != null) {
                PluginCommand command = Bukkit.getPluginCommand(alias);
                command.setExecutor(new LapisCoreCommandExecutor());
                if (tabCompleter != null)
                    command.setTabCompleter(tabCompleter);
                takenAliases.add(alias);
                takenAliases.addAll(command.getAliases());
            }
        }
        if (Bukkit.getPluginCommand(getName()) != null) {
            PluginCommand command = Bukkit.getPluginCommand(getName());
            if (!command.getPlugin().equals(core)) {
                command.setExecutor(new LapisCoreCommandExecutor());
                if (tabCompleter != null)
                    command.setTabCompleter(tabCompleter);
                takenAliases.addAll(command.getAliases());
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
     * Send message(s) from the messages.yml to the command sender provided,
     * requires {@link LapisCoreConfiguration} to be registered in {@link LapisCorePlugin}
     *
     * @param sender The command sender who you wish to send a message to
     * @param keys   The keys for the messages in the messages.yml
     */
    protected void sendMessage(CommandSender sender, String... keys) {
        for (String key : keys)
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

    /**
     * This is here to allow legacy commands to be moved over to this API easily
     *
     * @param sender       The sender of the command, could be a player or console
     * @param commandLabel The string representation of the form of the command being used
     * @param args         The arguments provided with the command
     */
    protected void onCommand(CommandSender sender, String commandLabel, String[] args) {
        onCommand(sender, args);
    }

    /**
     * Implement this to process commands
     *
     * @param sender The sender of the command, could be a player or console
     * @param args   The arguments provided with the command
     */
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
