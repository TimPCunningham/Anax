package io.github.timpcunningham.anax.events;

import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PermissionChangedEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    private Player player;
    private AnaxWorld world;

    public PermissionChangedEvent(Player player, AnaxWorld world) {
        this.player = player;
        this.world = world;
    }

    public Player getPlayer() {
        return this.player;
    }

    public AnaxWorld getWorld() {
        return this.world;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
