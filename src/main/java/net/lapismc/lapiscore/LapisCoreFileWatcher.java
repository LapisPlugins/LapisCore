package net.lapismc.lapiscore;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class LapisCoreFileWatcher {

    private LapisCorePlugin core;
    private BukkitTask task;
    private boolean stop;

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

    void stop() {
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
                core.config.reloadMessages(f);
                core.getLogger().info("Changes made to " + core.getName() + " messages.yml have been loaded");
                break;
            default:
                checkOtherFile(f);
        }
    }

    public void checkOtherFile(File f) {

    }

}
