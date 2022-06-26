/*
 * Copyright 2022 Benjamin Martin
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

public abstract class LapisCoreContextCalculator<Player> implements ContextCalculator<Player> {

    protected LapisCorePlugin plugin;

    public LapisCoreContextCalculator() {
        this.plugin = LapisCorePlugin.getInstance();
    }

}
