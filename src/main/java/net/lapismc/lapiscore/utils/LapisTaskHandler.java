/*
 * Copyright 2023 Benjamin Martin
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

import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing BukkitTasks so that they can be cleanly canceled when the plugin disables
 */
public class LapisTaskHandler {

    private final List<BukkitTask> tasks = new ArrayList<>();
    private final List<Runnable> shutdownTasks = new ArrayList<>();

    /**
     * Add a task so that it can be canceled later
     *
     * @param task The task to register
     */
    public void addTask(BukkitTask task) {
        tasks.add(task);
    }

    /**
     * Remove a task, this should be used if you manually cancel a task
     *
     * @param task The task to remove
     */
    public void removeTask(BukkitTask task) {
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
        tasks.forEach(BukkitTask::cancel);
        shutdownTasks.forEach(Runnable::run);
    }

}
