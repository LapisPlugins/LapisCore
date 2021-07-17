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

package net.lapismc.lapiscore;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class LapisCorePlugin extends JavaPlugin {

    private static LapisCorePlugin instance;
    public String primaryColor = ChatColor.GOLD.toString();
    public String secondaryColor = ChatColor.RED.toString();
    public LapisCoreConfiguration config;
    public LapisCorePermissions perms;

    public LapisCorePlugin() {
        instance = this;
    }

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

    /**
     * Get the normally protected class loader from the JavaPlugin class
     *
     * @return Returns this plugins class loader
     */
    public ClassLoader getPluginClassLoader() {
        return getClass().getClassLoader();
    }

    /**
     * Get the main instance of this class for use in Bukkit methods in places where dependency injection isn't viable
     * Should only be used when 100% necessary, don't use this if you can get a normal reference to the main class in some way
     *
     * @return The instance of {@link LapisCorePlugin}
     */
    public static LapisCorePlugin getInstance() {
        return instance;
    }

}
