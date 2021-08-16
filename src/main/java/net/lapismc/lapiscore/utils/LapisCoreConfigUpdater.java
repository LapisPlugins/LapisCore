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

package net.lapismc.lapiscore.utils;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.lapismc.lapiscore.LapisCorePlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class uses the ConfigUpdater by tchristofferson
 * This project can be found here
 * https://github.com/tchristofferson/Config-Updater
 */
public class LapisCoreConfigUpdater {

    /**
     * Adds new keys to configs without loosing comments
     *
     * @param plugin     The plugin where the configs are stored
     * @param newVersion The new ConfigVersion that we are upgrading to
     * @param f          The file we are updating
     */
    public LapisCoreConfigUpdater(LapisCorePlugin plugin, int newVersion, File f) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
        yaml.set("ConfigVersion", newVersion);
        try {
            yaml.save(f);
            ConfigUpdater.update(plugin, f.getName(), f, new ArrayList<>(Collections.singletonList("Permissions")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
