package net.lapismc.lapiscore;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LapisCoreEvent extends Event {

    public static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
