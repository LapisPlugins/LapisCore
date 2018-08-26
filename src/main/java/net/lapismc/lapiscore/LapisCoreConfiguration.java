/*
 * Copyright 2018 Benjamin Martin
 *
 * MICROSOFT REFERENCE SOURCE LICENSE (MS-RSL)
 *
 * This license governs use of the accompanying software. If you use the software, you accept this license. If you do not accept the license, do not use the software.
 * 1. Definitions
 *
 * The terms "reproduce," "reproduction" and "distribution" have the same meaning here as under U.S. copyright law.
 *
 * "You" means the licensee of the software.
 *
 * "Your company" means the company you worked for when you downloaded the software.
 *
 * "Reference use" means use of the software within your company as a reference, in read only form, for the sole purposes of debugging your products, maintaining your products, or enhancing the interoperability of your products with the software, and specifically excludes the right to distribute the software outside of your company.
 *
 * "Licensed patents" means any Licensor patent claims which read directly on the software as distributed by the Licensor under this license.
 * 2. Grant of Rights
 *
 * (A) Copyright Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive, worldwide, royalty-free copyright license to reproduce the software for reference use.
 *
 * (B) Patent Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive, worldwide, royalty-free patent license under licensed patents for reference use.
 * 3. Limitations
 *
 * (A) No Trademark License- This license does not grant you any rights to use the Licensor's name, logo, or trademarks.
 *
 * (B) If you begin patent litigation against the Licensor over patents that you think may apply to the software (including a cross-claim or counterclaim in a lawsuit), your license to the software ends automatically.
 *
 * (C) The software is licensed "as-is." You bear the risk of using it. The Licensor gives no express warranties, guarantees or conditions. You may have additional consumer rights under your local laws which this license cannot change. To the extent permitted under your local laws, the Licensor excludes the implied warranties of merchantability, fitness for a particular purpose and non-infringement.
 */

package net.lapismc.lapiscore;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

@SuppressWarnings("FieldCanBeLocal")
public class LapisCoreConfiguration {

    private int configVersion;
    private int messagesVersion;
    private LapisCorePlugin core;
    private File messagesFile;
    private YamlConfiguration messages;

    protected void registerCore(LapisCorePlugin core, int configVersion, int messagesVersion) {
        this.core = core;
        this.configVersion = configVersion;
        this.messagesVersion = messagesVersion;
        messagesFile = new File(core.getDataFolder() + File.separator + "messages.yml");
        generateConfigs();
        checkConfigVersions();
    }

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
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        core.primaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("primaryColor", ChatColor.GOLD.toString()));
        core.secondaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("secondaryColor", ChatColor.RED.toString()));
    }

    public void reloadMessages(File f) {
        messagesFile = f;
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        core.primaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("primaryColor", ChatColor.GOLD.toString()));
        core.secondaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("secondaryColor", ChatColor.RED.toString()));
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

    public YamlConfiguration getMessages() {
        return messages;
    }

    public String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(key).replace("&p", core.primaryColor).replace("&s", core.secondaryColor));
    }

}
