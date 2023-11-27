/*
 * Copyright 2023 Benjamin Martin
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

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * This interface provides a structure for command completion levels with the ability to dynamically fetch options and their children
 * This allows very complex and adaptable command completions including permission based completions
 */
public interface LapisTabOption {

    /**
     * Get a list of strings that should be displayed as options in the tab complete menu
     *
     * @param sender The CommandSender typing a command
     * @return A list of Strings which represent possible command arguments
     */
    List<String> getOptions(CommandSender sender);

    /**
     * Get the children of this option, these are LapisTabOption classes which may have their own children
     *
     * @param sender The CommandSender typing a command
     * @return A list of @{@link LapisTabOption} classes which represent the possible child options of the current option
     */
    List<LapisTabOption> getChildren(CommandSender sender);

}
