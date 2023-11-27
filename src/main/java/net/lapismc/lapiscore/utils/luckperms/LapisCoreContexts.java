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

package net.lapismc.lapiscore.utils.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for dealing with LuckPerms Contexts
 */
public class LapisCoreContexts {

    private final ContextManager contextManager;
    private final List<LapisCoreContextCalculator<Player>> registeredCalculators = new ArrayList<>();

    /**
     * Sets us up to manage contexts on the server if LuckPerms is installed
     */
    public LapisCoreContexts() {
        LuckPerms luckPerms = Bukkit.getServer().getServicesManager().load(LuckPerms.class);
        if (luckPerms == null) {
            throw new IllegalStateException("LuckPerms API not loaded.");
        }
        this.contextManager = luckPerms.getContextManager();
    }

    /**
     * Register a context calculator with LuckPerms, be sure to call {@link #unregisterAll()} on disable
     *
     * @param calculator The calculator you wish to register
     */
    public void registerContext(LapisCoreContextCalculator<Player> calculator) {
        contextManager.registerCalculator(calculator);
        registeredCalculators.add(calculator);
    }

    /**
     * Unregisters all contexts being managed by this class
     */
    public void unregisterAll() {
        registeredCalculators.forEach(contextManager::unregisterCalculator);
        registeredCalculators.clear();
    }

}
