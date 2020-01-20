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

package net.lapismc.lapiscore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class LapisArgumentTabCompleter implements TabCompleter {

    private List<LapisArgument> topLevelArguments;

    public LapisArgumentTabCompleter(List<LapisArgument> topLevelArguments) {
        this.topLevelArguments = topLevelArguments;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> possibleArguments = new ArrayList<>();
        if (args.length == 0) {
            for (LapisArgument argument : topLevelArguments) {
                possibleArguments.add(argument.getArgument());
            }
        } else {
            List<LapisArgument> currentLevelArguments = new ArrayList<>(topLevelArguments);
            //TODO: navigate down the lists of LapisArguments until there are no more or one isn't complete
            for (String arg : args) {
                for (LapisArgument currentArg : currentLevelArguments) {
                    if (currentArg.getArgument().equalsIgnoreCase(arg)) {
                        //TODO: Navigate down a level and then break so that we check the next arg
                        currentLevelArguments = currentArg.getChildren();
                        break;
                    }
                }
            }
            for (LapisArgument arg : currentLevelArguments) {
                possibleArguments.add(arg.getArgument());
            }
        }
        return possibleArguments;
    }
}
