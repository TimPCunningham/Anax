package io.github.timpcunningham.anax.listeners;

import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Playerlisteners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        AnaxPlayerManager.getInstance().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        AnaxPlayerManager.getInstance().removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        AnaxPlayerManager.getInstance().removePlayer(event.getPlayer().getUniqueId());
    }
}
