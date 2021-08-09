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

import net.lapismc.lapiscore.LapisCorePlugin;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * Class used to check for updates on Spigot resources
 * Useful for closed source/premium plugins
 */
public class SpigotUpdateChecker {

    private final LapisCorePlugin plugin;
    private final String resourceID;

    /**
     * Setup the Update Checker
     *
     * @param plugin     The plugin that you wish to report to
     * @param resourceID The Spigot resource ID e.g. LapisBans = 58896
     */
    public SpigotUpdateChecker(LapisCorePlugin plugin, String resourceID) {
        this.plugin = plugin;
        this.resourceID = resourceID;
    }

    /**
     * Check if there is an update available on Spigot
     *
     * @return Returns true if there is a different version as the latest on spigot, false if the versions match
     */
    public boolean isUpdateAvailable() {
        String newestVersion = getNewestVersionString();
        if (newestVersion == null || newestVersion.equals("")) {
            return false;
        } else {
            return !newestVersion.equals(plugin.getDescription().getVersion());
        }
    }

    /**
     * Get the string for the latest version, this could be used when informing the user about an update to show how far behind they are
     *
     * @return Returns the latest version on spigot
     */
    public String getNewVersion() {
        return getNewestVersionString();
    }

    private String getNewestVersionString() {
        return getFromURL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceID);
    }

    private String getFromURL(String stringUrl) {
        String response = "";
        try {
            URL url = new URL(stringUrl);
            Scanner s = new Scanner(url.openStream());
            if (s.hasNext()) {
                response = s.next();
                s.close();
            }
        } catch (IOException ignored) {
        }
        return response;
    }

}
