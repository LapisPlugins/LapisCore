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

import net.lapismc.lapiscore.LapisCorePlugin;
import net.luckperms.api.context.ContextCalculator;

/**
 * An abstract class for implementing LuckPerms contexts on players
 *
 * @param <Player> Player since these contexts are to be applied to players
 */
public abstract class LapisCoreContextCalculator<Player> implements ContextCalculator<Player> {

    /**
     * Plugin instance to be used by the implementing class
     */
    protected LapisCorePlugin plugin;

    /**
     * Simple constructor that sets the plugin object to the core plugin instance
     */
    public LapisCoreContextCalculator() {
        this.plugin = LapisCorePlugin.getInstance();
    }

}
