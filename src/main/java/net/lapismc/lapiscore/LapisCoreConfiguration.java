/*
 * Copyright 2019 Benjamin Martin
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
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

/**
 * An utility class for dealing with config.yml and messages.yml files
 */
@SuppressWarnings("FieldCanBeLocal")
public class LapisCoreConfiguration {

    private int configVersion;
    private int messagesVersion;
    private LapisCorePlugin core;
    private File messagesFile;
    private YamlConfiguration messages;

    /**
     * Register the configurations and generate them
     *
     * @param core            The LapisCorePlugin that the files should be loaded for
     * @param configVersion   The current config version for the config.yml
     * @param messagesVersion The current config version for the messages.yml
     */
    public LapisCoreConfiguration(LapisCorePlugin core, int configVersion, int messagesVersion) {
        this.core = core;
        this.configVersion = configVersion;
        this.messagesVersion = messagesVersion;
        messagesFile = new File(core.getDataFolder() + File.separator + "messages.yml");
        generateConfigs();
        checkConfigVersions();
    }

    /**
     * Generates the default configs from the plugin jar file
     * Will not overwrite existing files, will only generate them if they don't exist
     */
    public void generateConfigs() {
        core.saveDefaultConfig();
        if (!messagesFile.exists()) {
            try (InputStream is = core.getResource("messages.yml");
                 OutputStream os = new FileOutputStream(messagesFile)) {
                int readBytes;
                byte[] buffer = new byte[4096];
                while ((readBytes = is.read(buffer)) > 0) {
                    os.write(buffer, 0, readBytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reloadMessages(messagesFile);
    }

    /**
     * Load the given file into memory as the messages.yml
     *
     * @param f The messages.yml file
     */
    public void reloadMessages(File f) {
        messagesFile = f;
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        core.primaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("PrimaryColor", ChatColor.GOLD.toString()));
        core.secondaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("SecondaryColor", ChatColor.RED.toString()));
    }

    private void checkConfigVersions() {
        if (core.getConfig().getInt("ConfigVersion") != configVersion) {
            File oldConfig = new File(core.getDataFolder() + File.separator + "config_PRE_" + core.getDescription().getVersion() + ".yml");
            File config = new File(core.getDataFolder() + File.separator + "config.yml");
            if (oldConfig.exists()) {
                oldConfig.delete();
            }
            config.renameTo(oldConfig);
            if (config.exists()) {
                config.delete();
            }
            core.saveDefaultConfig();
            core.getLogger().info("The config.yml file has been updated, it is now called config_PRE_" + core.getDescription().getVersion() + ".yml," +
                    " please transfer any values into the new config.yml");
        }
        if (messages.getInt("ConfigVersion") != messagesVersion) {
            File oldMessages = new File(core.getDataFolder() + File.separator + "messages_PRE_" + core.getDescription().getVersion() + ".yml");
            if (oldMessages.exists()) {
                oldMessages.delete();
            }
            messagesFile.renameTo(oldMessages);
            if (messagesFile.exists()) {
                messagesFile.delete();
            }
            generateConfigs();
            core.getLogger().info("The messages.yml file has been updated, it is now called messages_PRE_" + core.getDescription().getVersion() + ".yml," +
                    " please transfer any values into the new messages.yml");
        }
    }

    /**
     * Get the messages.yml as a {@link YamlConfiguration}
     *
     * @return Returns the messages.yml as a YamlConfiguration
     */
    public YamlConfiguration getMessages() {
        return messages;
    }

    /**
     * Get a message from the messages.yml, This method will colorize the file when it is loaded
     *
     * @param key The key in the messages.yml
     * @return Returns a colorized string from the given key in the messages.yml
     */
    public String getMessage(String key) {
        return colorMessage(messages.getString(key, "&sError retrieving message from config"));
    }

    /**
     * Colorize any string with color codes, this is used to support the p and s color codes that you might retrieve directly
     *
     * @param msg The string you wish to colorize
     * @return Returns a colored string
     */
    public String colorMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg.replace("&p", core.primaryColor)
                .replace("&s", core.secondaryColor));
    }

}
