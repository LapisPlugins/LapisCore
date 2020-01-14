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

import net.lapismc.lapiscore.LapisCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * An utility class for reloading files when they are edited
 */
public class LapisCoreFileWatcher {

    private LapisCorePlugin core;
    private BukkitTask task;
    private boolean stop;

    /**
     * Start the file watcher
     *
     * @param core The LapisCorePlugin that the file watcher should be registered to
     */
    public LapisCoreFileWatcher(LapisCorePlugin core) {
        this.core = core;
        start();
    }

    private void start() {
        task = Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            try {
                watcher();
            } catch (IOException | InterruptedException e) {
                core.getLogger().warning(core.getName() + " file watcher has stopped," +
                        " configs wont be reloaded until the server restarts");
            }
        });
    }

    /**
     * Used to safely stop the file watcher
     */
    public void stop() {
        stop = true;
        task.cancel();
    }

    private void watcher() throws IOException, InterruptedException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(core.getDataFolder().getAbsolutePath());
        dir.register(watcher, ENTRY_DELETE, ENTRY_MODIFY);
        core.getLogger().info(core.getName() + " file watcher started!");
        WatchKey key = watcher.take();
        while (key != null && !stop) {
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                File f = fileName.toFile();
                if (kind == ENTRY_DELETE) {
                    if (f.getName().endsWith(".yml")) {
                        String name = f.getName().replace(".yml", "");
                        switch (name) {
                            case "config":
                                core.saveDefaultConfig();
                                core.reloadConfig();
                                break;
                            case "messages":
                                core.config.generateConfigs();
                                break;
                        }
                    }
                } else if (kind == ENTRY_MODIFY) {
                    if (f.getName().endsWith(".yml")) {
                        checkConfig(f);
                    }
                }
            }
            key.reset();
            if (!stop) {
                key = watcher.take();
            }
        }
        if (!stop)
            throw new IOException("File watcher failed");
    }

    private void checkConfig(File f) {
        String name = f.getName().replace(".yml", "");
        switch (name) {
            case "config":
                core.reloadConfig();
                if (core.perms != null)
                    core.perms.loadPermissions();
                core.getLogger().info("Changes made to the " + core.getName() + " config have been loaded");
                break;
            case "messages":
                core.config.reloadMessages();
                core.getLogger().info("Changes made to " + core.getName() + " messages.yml have been loaded");
                break;
            default:
                checkOtherFile(f);
        }
        fileUpdate(f);
    }

    /**
     * Override this method to deal with a file being edited
     * This will be fired for every file change, including config.yml and messages.yml files
     *
     * @param f The file that has been updated
     */
    public void fileUpdate(File f) {

    }

    /**
     * Override this method to deal with an unhandled file being edited
     * This will only be fired for unknown files, it will not trigger when config or messages files are edited
     *
     * @param f The file that has been updated
     */
    public void checkOtherFile(File f) {

    }

}
