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

package net.lapismc.lapiscore.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class LapisArgumentTabCompleter implements TabCompleter {

    private final List<LapisArgument> topLevelArguments;

    public LapisArgumentTabCompleter(List<LapisArgument> topLevelArguments) {
        this.topLevelArguments = topLevelArguments;
    }

    public void addTopLevelArguments(List<LapisArgument> args) {
        topLevelArguments.addAll(args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> possibleArguments = new ArrayList<>();
        if (args.length == 0) {
            for (LapisArgument argument : topLevelArguments) {
                possibleArguments.add(argument.getArgument());
            }
        } else {
            // /help player (name)
            // /help pl     Should complete to player
            // /help player     Should show all player names
            List<LapisArgument> currentLevelArguments = new ArrayList<>(topLevelArguments);
            int depth = 1;
            //Check if we are at the last argument, this will decide if we navigate down or try to offer completions
            while (args.length >= depth) {
                //This key is the complete argument, in the example its player
                String key = args[depth - 1];
                for (LapisArgument arg : currentLevelArguments) {
                    if (arg.getArgument().equalsIgnoreCase(key)) {
                        //Navigate down a level
                        currentLevelArguments = arg.getChildren();
                        depth++;
                        break;
                    }
                }
            }
            //At this point we are offering solutions
            //Filter out suggestions that dont start with the current argument
            int finalDepth = depth;
            currentLevelArguments.removeIf(lapisArgument -> !lapisArgument.getArgument().toLowerCase()
                    .startsWith(args[finalDepth - 1].toLowerCase()));
            possibleArguments.addAll(lapisArgumentsToStrings(currentLevelArguments));
        }
        return possibleArguments;
    }

    private List<String> lapisArgumentsToStrings(List<LapisArgument> args) {
        List<String> list = new ArrayList<>();
        for (LapisArgument argument : args) {
            list.add(argument.getArgument());
        }
        return list;
    }
}
