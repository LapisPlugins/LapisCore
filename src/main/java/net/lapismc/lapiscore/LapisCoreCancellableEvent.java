package net.lapismc.lapiscore;

import org.bukkit.event.Cancellable;

public class LapisCoreCancellableEvent extends LapisCoreEvent implements Cancellable {

    private String reason;
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Deprecated
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public void setCancelled(boolean cancel, String reason) {
        cancelled = cancel;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
