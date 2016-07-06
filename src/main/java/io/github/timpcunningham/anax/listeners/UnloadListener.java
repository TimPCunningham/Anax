package io.github.timpcunningham.anax.listeners;

import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.server.Debug;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class UnloadListener extends BukkitRunnable implements Listener {
    private static UnloadListener self;
    private static Map<String, Date> emptyWorlds;

    //TODO - Make Singleton
    private UnloadListener() {
        emptyWorlds = new HashMap<>();
    }

    public static UnloadListener getInstance() {
        if(self == null) {
            self = new UnloadListener();
        }
        return self;
    }

    public void add(String world) {
        if(world.equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())) {
            return;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, Anax.get().getConfig().getInt("unload.period"));
        emptyWorlds.put(world, c.getTime());
    }

    public void remove(String world) {
        emptyWorlds.remove(world);
    }

    public boolean isEmpty(String world) {
        return emptyWorlds.containsKey(world);
    }

    @Override
    public void run() {
        List<String> worlds = new ArrayList<>(emptyWorlds.keySet());
        Chat.alertConsole("Checking for worlds to be unloaded...");

        worlds.forEach(world -> {
            if(emptyWorlds.get(world).before(new Date())) {
                emptyWorlds.remove(world);
                AnaxWorld unload = AnaxWorldManagement.getInstance().getWorld(world);
                AnaxWorldManagement.getInstance().unloadWorld(unload);
                Chat.alertConsole(Lang.WORLD_UNLOADED, world);
            }
        });
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        World to = event.getPlayer().getWorld();
        World from = event.getFrom();

        if(from.getPlayers().size() == 0) {
            add(from.getName());
        }
        if(isEmpty(to.getName())) {
            remove(to.getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String world = event.getPlayer().getWorld().getName();

        if(isEmpty(world)) {
            remove(world);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(event.getPlayer().getWorld().getPlayers().size() == 0) {
            add(event.getPlayer().getWorld().getName());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        World world = event.getPlayer().getWorld();

        if(world.getPlayers().size() == 1) {
            add(world.getName());
        }
    }
}
