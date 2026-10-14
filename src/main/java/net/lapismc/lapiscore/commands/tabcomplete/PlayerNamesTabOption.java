/*
 * Copyright 2026 Benjamin Martin
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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerNamesTabOption implements LapisTabOption {

    private final boolean includeOffline;
    private final List<LapisTabOption> childOptions;

    public PlayerNamesTabOption(boolean includeOffline) {
        this(includeOffline, new ArrayList<>());
    }

    public PlayerNamesTabOption(boolean includeOffline, List<LapisTabOption> childOptions) {
        this.includeOffline = includeOffline;
        this.childOptions = childOptions;
    }

    @Override
    public List<String> getOptions(CommandSender sender) {
        List<String> names = new ArrayList<>();
        if (includeOffline) {
            Arrays.stream(Bukkit.getServer().getOfflinePlayers()).toList().forEach(offlinePlayer -> names.add(offlinePlayer.getName()));
        } else {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> names.add(player.getName()));
        }
        return names;
    }

    @Override
    public List<LapisTabOption> getChildren(CommandSender sender) {
        return childOptions;
    }
}
