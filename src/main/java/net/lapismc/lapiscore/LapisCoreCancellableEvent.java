/*
 * Copyright 2018 Benjamin Martin
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

import org.bukkit.event.Cancellable;

/**
 * An utility class to make cancellable events
 */
public class LapisCoreCancellableEvent extends LapisCoreEvent implements Cancellable {

    private String reason;
    private boolean cancelled = false;

    /**
     * Check if the event is cancelled
     *
     * @return Returns true if the event has been cancelled by an event listener
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Please use {@link #setCancelled(boolean, String)} to provide a reason for why you are cancelling the event
     *
     * @param cancel The boolean value for the cancelled state of the event
     */
    @Deprecated
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    /**
     * Cancel the event with a reason
     *
     * @param cancel True if the event should be cancelled
     * @param reason The reason to be sent to the player as to why the event was cancelled, Not all plugins implement this
     */
    public void setCancelled(boolean cancel, String reason) {
        cancelled = cancel;
        this.reason = reason;
    }

    /**
     * Get the reason from {@link #setCancelled(boolean, String)}
     *
     * @return Returns the String reason as to why the event was cancelled, This will probably be null if the event is not cancelled
     */
    public String getReason() {
        return reason;
    }

}
