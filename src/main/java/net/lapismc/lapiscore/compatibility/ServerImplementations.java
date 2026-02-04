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

package net.lapismc.lapiscore.compatibility;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for detecting which implementation of the Minecraft server we are running on
 */
public class ServerImplementations {

    /**
     * Get a list of the current implementations, use contains for similar to check if you can use certain methods
     *
     * @return a list of currently available implementations
     */
    public List<imp> getImplementations() {
        List<imp> implementations = new ArrayList<>();
        try {
            Class.forName("org.bukkit.Bukkit");
            implementations.add(imp.Bukkit);
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Class.forName("org.bukkit.Server.Spigot");
            implementations.add(imp.Spigot);
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Class.forName("com.destroystokyo.paper.utils.PaperPluginLogger");
            implementations.add(imp.Paper);
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            implementations.add(imp.Folia);
        } catch (ClassNotFoundException ignored) {
        }
        return implementations;
    }

    /**
     * This enum is the kinds of implementations we can detect
     */
    public enum imp {Bukkit, Spigot, Paper, Folia}

}
