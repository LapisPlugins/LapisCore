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

package net.lapismc.lapiscore.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {

    private static final List<LapisCoreCommand> registeredCommands = new ArrayList<>();

    public static void registerCommand(LapisCoreCommand command) {
        registeredCommands.add(command);
    }

    public static LapisCoreCommand getCommand(String name) {
        for (LapisCoreCommand command : registeredCommands) {
            if (command.getName().equalsIgnoreCase(name) || command.getAliases().contains(name)) {
                return command;
            }
        }
        return null;
    }

}
