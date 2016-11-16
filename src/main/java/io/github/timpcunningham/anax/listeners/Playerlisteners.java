package io.github.timpcunningham.anax.listeners;

import io.github.timpcunningham.anax.events.PermissionChangedEvent;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.player.PlayerUtils;
import io.github.timpcunningham.anax.utils.server.Debug;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.types.Access;
import io.github.timpcunningham.anax.world.types.RoleType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

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

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        AnaxWorld world = AnaxWorldManagement.getInstance().getWorld(event.getTo().getWorld().getName());

        if(!WorldUtils.canVisit(player, world)) {
            Chat.alertPlayer(player, Lang.WORLD_DENY_ACCESS, null);
            event.setCancelled(true);

            if(event.getFrom().getWorld().equals(world.getWorld())) {
                Location spawn = AnaxWorldManagement.getInstance().getDefaultWorld().getSpawn();

                player.teleport(spawn);
            }
        }
    }
}
