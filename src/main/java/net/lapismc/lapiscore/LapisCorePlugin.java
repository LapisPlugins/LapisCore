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

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class LapisCorePlugin extends JavaPlugin {

    public String primaryColor = ChatColor.GOLD.toString();
    public String secondaryColor = ChatColor.RED.toString();
    public LapisCoreConfiguration config;
    public LapisCorePermissions perms;

    /**
     * Register a {@link LapisCoreConfiguration} class to be accessed with this.config
     *
     * @param config The configuration class you wish to register
     */
    public void registerConfiguration(LapisCoreConfiguration config) {
        this.config = config;
    }

    /**
     * Register a {@link LapisCorePermissions} class to be accessed with this.perms
     *
     * @param perms The permissions class you wish to register
     */
    public void registerPermissions(LapisCorePermissions perms) {
        this.perms = perms;
    }

}
