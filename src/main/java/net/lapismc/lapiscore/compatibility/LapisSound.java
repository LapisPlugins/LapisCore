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

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

/**
 * This is a class used for loading a sound from a config value
 * It handles the name to find the correct sound from the current Minecraft version
 */
public class LapisSound {

    private final String soundName;
    private final String namespace;

    /**
     * Load a sound with the given name
     *
     * @param soundName Can be either a Minecraft namespaced key or an old Enum
     */
    public LapisSound(String soundName) {
        String nameSpace = NamespacedKey.MINECRAFT_NAMESPACE;
        this(nameSpace, soundName);
    }

    /**
     * Load a sound with a namespace and key
     *
     * @param namespace The namespace to load from, useful for resource packs
     * @param key       The key to load
     */
    public LapisSound(String namespace, String key) {
        this.namespace = namespace;
        this.soundName = key;
    }

    /**
     * Get the sound from the sound name provided in the constructor
     *
     * @return the sound if one with this name exists, otherwise null
     */
    public Sound getSound() {
        if (soundName == null || soundName.isEmpty())
            return null;
        Sound sound = null;
        try {
            //Check if the sound name is a namespaced key
            NamespacedKey soundKey = new NamespacedKey(namespace, soundName);
            //noinspection deprecation
            Registry<Sound> soundRegistry = Bukkit.getRegistry(Sound.class);
            if (soundRegistry == null)
                return null;
            sound = soundRegistry.get(soundKey);
        } catch (NoSuchMethodError | IllegalArgumentException e) {
            //This means we aren't on paper or are on an older version
            //or
            //The namespaced key wasn't valid
        }
        if (sound == null) {
            //The key was incorrect, or we are on an old version
            //Check for it in the old system
            //noinspection UnstableApiUsage,removal
            sound = Sound.valueOf(soundName);
        }
        return sound;
    }

}
