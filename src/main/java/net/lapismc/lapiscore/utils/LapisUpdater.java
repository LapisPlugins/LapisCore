/*
 * Copyright 2020 Benjamin Martin
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


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Logger;

/**
 * An utility class to check and download plugin updates using GitHub
 */
public class LapisUpdater {

    private final String ID;
    private final String jarName;
    private final String username;
    private final String repoName;
    private final String branch;
    private final JavaPlugin plugin;
    private final Logger logger;
    private Boolean force;
    private String newVersion;

    /**
     * The URL to the latest jar should be https://raw.githubusercontent.com/username/repoName/branch/updater/ID/jarName.jar
     *
     * @param plugin   The main class of your plugin
     * @param jarName  The name of the jar file, excludes .jar
     * @param username The github username used for the repo
     * @param repoName The repo name
     * @param branch   The branch name
     */
    public LapisUpdater(JavaPlugin plugin, String jarName, String username, String repoName, String branch) {
        this.plugin = plugin;
        this.ID = plugin.getName();
        this.jarName = jarName;
        this.username = username;
        this.repoName = repoName;
        this.branch = branch;
        this.logger = Bukkit.getLogger();
    }

    /**
     * Check if there is an update
     *
     * @return True if there is an update
     */
    public boolean checkUpdate() {
        this.force = false;
        return updateCheck();
    }

    /**
     * Downloads the latest jar (if there is an update) and readies it for installation
     */
    public void downloadUpdate() {
        this.force = true;
        downloadUpdateJar();
    }

    private void downloadUpdateJar() {
        if (updateCheck()) {
            try {
                URL changeLogURL = new URL(
                        "https://raw.githubusercontent.com/" + username + "/" + repoName + "/" + branch + "/updater" +
                                "/changelog.yml");
                File changeLogFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator +
                        "changelog.yml");
                URL jarURL = new URL(
                        "https://raw.githubusercontent.com/" + username + "/" + repoName + "/" + branch + "/updater/"
                                + ID + "/" + jarName + ".jar");
                File update = plugin.getServer().getUpdateFolderFile();
                if (!update.exists()) {
                    if (!update.mkdir()) {
                        logger.severe("Failed to generate " + update.getName());
                    }
                }
                File jar = new File(update.getAbsolutePath() + File.separator + jarName + ".jar");
                new FileDownloader().downloadFile(changeLogURL, changeLogFile).downloadFile(jarURL, jar);
                YamlConfiguration changeLog = YamlConfiguration.loadConfiguration(changeLogFile);
                logger.info("Changes in newest Version \n" +
                        changeLog.getStringList(newVersion).toString().replace("[", "").replace("]", ""));
            } catch (IOException e) {
                logger.severe("HomeSpawn updater failed to download updates!");
                logger.severe("Please check your internet connection and" +
                        " firewall settings and try again later");
            }
        }
    }

    private boolean updateCheck() {
        String oldVersion;
        String newVersion;
        File updateFile;
        YamlConfiguration yaml;
        try {
            URL remoteUpdate = new URL(
                    "https://raw.githubusercontent.com/" + username + "/" + repoName + "/" + branch + "/updater" +
                            "/update.yml");
            updateFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator +
                    "update.yml");
            Date d = new Date(updateFile.lastModified());
            Date d0 = new Date();
            d0.setTime(d0.getTime() - 3600);
            if (!updateFile.exists() || force || d.before(d0)) {
                new FileDownloader().downloadFile(remoteUpdate, updateFile);
                if (!updateFile.setLastModified(d0.getTime())) {
                    logger.info("Failed to set modified time for " + updateFile.getName());
                }
            }
        } catch (IOException e) {
            logger.severe("Failed to check for updates!");
            logger.severe("Please check your internet and firewall settings" +
                    " and try again later!");
            return false;
        }
        try {
            yaml = YamlConfiguration.loadConfiguration(updateFile);
            if (!yaml.contains(ID)) {
                return false;
            }
            oldVersion = plugin.getDescription().getVersion();
            newVersion = yaml.getString(ID);
        } catch (Exception e) {
            logger.severe("Failed to load update.yml or parse the values!" +
                    " It may be corrupt!");
            logger.severe("Please try again later");
            if (!updateFile.delete()) {
                logger.info("Failed to delete " + updateFile.getName());
            }
            return false;
        }
        this.newVersion = newVersion;
        return !oldVersion.equals(newVersion);
    }
}
