/*
 * Copyright 2025 Benjamin Martin
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

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.lapismc.lapiscore.LapisCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing BukkitTasks so that they can be cleanly canceled when the plugin disables
 * Folia compatibility adapted from <a href="https://mineacademy.org/tutorial-38/">MineAcademy</a>
 * NOTE: Tasks on Folia can lock the global region, hence Async uses threads in run task
 */
public class LapisTaskHandler {

    private final LapisCorePlugin plugin;
    private final List<LapisTask> tasks = new ArrayList<>();
    private boolean isFolia;
    private final List<Runnable> shutdownTasks = new ArrayList<>();

    /**
     * Initialize the task handler with the core plugin for creating and scheduling tasks
     *
     * @param plugin The core plugin that tasks should be scheduled with
     */
    public LapisTaskHandler(LapisCorePlugin plugin) {
        this.plugin = plugin;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;

        } catch (final ClassNotFoundException e) {
            isFolia = false;
        }
    }

    /**
     * Run a task on the next tick
     *
     * @param runnable The task to run
     * @param isAsync  Should the task be async (Bukkit Only)
     */
    public LapisTask runTask(Runnable runnable, boolean isAsync) {
        if (isFolia) {
            if (isAsync)
                return new LapisTask(new LapisThread(runnable));
            else {
                return new LapisTask(Bukkit.getGlobalRegionScheduler().run(plugin, t -> runnable.run()));
            }
        } else {
            if (isAsync)
                return new LapisTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
            else
                return new LapisTask(Bukkit.getScheduler().runTask(plugin, runnable));
        }
    }

    /**
     * Run a task later
     *
     * @param runnable   The task to run
     * @param delayTicks The delay before it is run in game ticks
     * @param isAsync    Should the task be async (Bukkit Only)
     * @return a LapisTask object that can be used to cancel the task
     */
    public LapisTask runTaskLater(Runnable runnable, long delayTicks, boolean isAsync) {
        if (isFolia)
            return new LapisTask(Bukkit.getGlobalRegionScheduler().runDelayed(plugin,
                    t -> runnable.run(), delayTicks));
        else {
            if (isAsync)
                return new LapisTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks));
            else
                return new LapisTask(Bukkit.getScheduler().runTaskLater(plugin,
                        runnable, delayTicks));
        }
    }

    /**
     * Run a task repeatedly
     *
     * @param runnable    The task to run
     * @param delayTicks  The delay before first run in ticks
     * @param periodTicks The delay between each run in ticks
     * @param isAsync     Should the task be async (Bukkit Only)
     * @return a LapisTask object that can be used to cancel the task
     */
    public LapisTask runTaskTimer(Runnable runnable, long delayTicks, long periodTicks, boolean isAsync) {
        if (isFolia)
            return new LapisTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin,
                    t -> runnable.run(), delayTicks, periodTicks));
        else {
            if (isAsync)
                return new LapisTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delayTicks, periodTicks));
            else
                return new LapisTask(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks));
        }
    }

    /**
     * Add a task so that it can be canceled later
     *
     * @param task The task to register
     */
    public void addTask(LapisTask task) {
        tasks.add(task);
    }

    /**
     * Remove a task, this should be used if you manually cancel a task
     *
     * @param task The task to remove
     */
    public void removeTask(LapisTask task) {
        tasks.remove(task);
    }

    /**
     * Adds a task to be run when the plugin is disabled
     *
     * @param task The Runnable to run
     */
    public void addShutdownTask(Runnable task) {
        shutdownTasks.add(task);
    }


    /**
     * Should only be called from on disable as it will cancel all registered commands
     */
    public void stopALlTasks() {
        tasks.forEach(LapisTask::cancel);
        shutdownTasks.forEach(Runnable::run);
    }

    /**
     * A class to represent tasks for Folia or Bukkit
     */
    public static class LapisTask {

        private ScheduledTask foliaTask;
        private LapisThread thread;
        private BukkitTask bukkitTask;

        /**
         * Register a task from Folia
         *
         * @param foliaTask The task to register
         */
        LapisTask(ScheduledTask foliaTask) {
            this.foliaTask = foliaTask;
        }

        /**
         * Register a LapisThread from TaskHandler
         *
         * @param thread The thread to register
         */
        LapisTask(LapisThread thread) {
            this.thread = thread;
            thread.start();
        }

        /**
         * Register a BukkitTask from Bukkit
         *
         * @param bukkitTask The task to register
         */
        LapisTask(BukkitTask bukkitTask) {
            this.bukkitTask = bukkitTask;
        }

        /**
         * Cancel the task
         */
        public void cancel() {
            if (foliaTask != null)
                foliaTask.cancel();
            else if (bukkitTask != null)
                bukkitTask.cancel();
            else if (thread != null)
                thread.cancel();
        }
    }

    /**
     * An implementation of Thread to support canceling via LapisTask class
     */
    public static class LapisThread extends Thread {

        /**
         * This value can be access from inside the thread
         * The task should stop at its earliest convince when this is true
         */
        protected boolean shouldStop = false;

        /**
         * Create a thread
         *
         * @param runnable The runnable that will be executed on the new thread
         */
        public LapisThread(Runnable runnable) {
            super(runnable);
        }

        /**
         * Cancel the thread
         */
        public void cancel() {
            shouldStop = true;
            this.interrupt();
        }

    }

}
