package io.github.timpcunningham.anax.listeners;

import io.github.timpcunningham.anax.events.PermissionChangedEvent;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Playerlisteners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        AnaxWorld world = AnaxWorldManagement.getInstance().getWorld(event.getPlayer().getWorld().getName());
        AnaxPlayerManager.getInstance().addPlayer(event.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(new PermissionChangedEvent(event.getPlayer(), world));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        AnaxPlayerManager.getInstance().removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        AnaxPlayerManager.getInstance().removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        AnaxWorld world = AnaxWorldManagement.getInstance().getWorld(event.getPlayer().getWorld().getName());
        Bukkit.getServer().getPluginManager().callEvent(new PermissionChangedEvent(event.getPlayer(), world));
    }
}
