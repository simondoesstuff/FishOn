package com.simondoestuff.fishon.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class SwimChangeEvent extends Event implements Listener {
    private static final HandlerList handlers = new HandlerList();
    private boolean swimming;
    private Player player;

    public SwimChangeEvent(Player player, boolean swimming) {
        this.swimming = swimming;
        this.player = player;
    }

    public boolean isSwimming() {
        return swimming;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
