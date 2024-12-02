/*
 * Copyright 2024 Benjamin Martin
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

package net.lapismc.lapiscore.commands.tabcomplete;

import net.lapismc.lapiscore.commands.CommandRegistry;
import net.lapismc.lapiscore.commands.LapisCoreCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class to handle dynamic and complex tab completions for @{@link LapisCoreCommand} classes
 */
public class LapisCoreTabCompleter implements TabCompleter {

    HashMap<LapisCoreCommand, List<LapisTabOption>> topLevelOptions = new HashMap<>();

    /**
     * Register the top level options for the given command
     *
     * @param command The command that these completions should be shown on
     * @param options The top level options for this command, children should be stored within each option
     */
    public void registerTopLevelOptions(LapisCoreCommand command, List<LapisTabOption> options) {
        topLevelOptions.put(command, options);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        LapisCoreCommand cmd = null;
        //Find which LapisCore Command this command is meant to be
        if (!(command instanceof LapisCoreCommand)) {
            //It's a command we have taken the alias of, we need to find our command class
            for (LapisCoreCommand possibleCommand : CommandRegistry.getAllCommands()) {
                if (command.getName().equalsIgnoreCase(possibleCommand.getName())) {
                    //The commands name matches, so we know this is the correct command
                    cmd = possibleCommand;
                    break;
                }
                for (String takenAlias : possibleCommand.getTakenAliases()) {
                    if (takenAlias.equalsIgnoreCase(command.getName())) {
                        //The alias matches one that we took, so we know this is the correct command
                        cmd = possibleCommand;
                        break;
                    }
                }
                //The command was found in the alias loop, so we break here to save searching other commands
                if (cmd != null)
                    break;
            }
            //We couldn't find the command
            if (cmd == null)
                return null;
        } else {
            cmd = (LapisCoreCommand) command;
        }
        List<String> result = new ArrayList<>();
        List<LapisTabOption> options = topLevelOptions.get(cmd);
        //If args length is 0, then they haven't typed any arguments at all yet, so we just show all top level options
        if (args.length == 0) {
            for (LapisTabOption option : options) {
                result.addAll(option.getOptions(sender));
            }
        } else {
            //If they have typed any arguments, we start the recursive process of checking them
            return recursiveSearch(options, sender, new ArrayList<>(List.of(args)));
        }
        return result;
    }

    private List<String> recursiveSearch(List<LapisTabOption> options, CommandSender sender, List<String> args) {
        //The list we will return with completions
        List<String> result = new ArrayList<>();
        //If options is null or empty then we have reached the end of the possibilities, so we return an empty list
        //If args is empty then they haven't started typing the next word yet, so we also return an empty list
        if (options == null || options.isEmpty() || args.isEmpty())
            return result;
        for (LapisTabOption option : options) {
            for (String s : option.getOptions(sender)) {
                //Null check the option, this shouldn't need to be handled, but somehow it has happened
                if (s == null)
                    continue;
                //Check if this is complete, if it is we need to go down another level
                if (s.equalsIgnoreCase(args.get(0))) {
                    //Get the options one level down the tree
                    List<LapisTabOption> children = option.getChildren(sender);
                    //Clone the arguments and then remove item 0 so that the option below only sees the relevant arguments
                    List<String> clonedArgs = new ArrayList<>(args);
                    clonedArgs.remove(0);
                    //Send these new arguments to the children to check again
                    return recursiveSearch(children, sender, clonedArgs);
                }
                //We know that they haven't completed the argument, so lets check what options they have
                if (s.startsWith("(") && s.endsWith(")")) {
                    //It's a free from item like a name, show the suggestion
                    result.add(s);
                } else if (s.toLowerCase().startsWith(args.get(0).toLowerCase())) {
                    //What they have typed so far partly matches a possible option, add it to the results
                    result.add(s);
                }
            }
        }
        return result;
    }
}
