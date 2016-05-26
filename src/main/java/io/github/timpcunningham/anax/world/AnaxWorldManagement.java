package io.github.timpcunningham.anax.world;

import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class AnaxWorldManagement {
    private static AnaxWorldManagement self;
    private Map<String, AnaxWorld> loadedWorlds;

    private AnaxWorldManagement() {
        loadedWorlds = new HashMap<>();
    }

    public static AnaxWorldManagement getInstance() {
        if(self == null) {
            self = new AnaxWorldManagement();
        }
        return self;
    }

    public Collection<AnaxWorld> getWorlds() {
        return loadedWorlds.values();
    }

    public List<AnaxWorld> getPlayerWorlds(UUID uuid) {
        return loadedWorlds.values().stream()
                .filter(anaxWorld -> anaxWorld.getMemeberList(RoleType.OWNER).contains(uuid))
                .collect(Collectors.toList());
    }

    public AnaxWorld getWorld(String worldName) {
        return loadedWorlds.get(worldName);
    }

    public boolean isLoadedWorld(String worldName) {
        return loadedWorlds.containsKey(worldName);
    }

    public AnaxWorld loadWorld(AnaxWorld world) throws LocalizedException {
        if(WorldUtils.isBukkitWorldLoaded(world.getWorldName())) {
            throw new LocalizedException(Lang.WORLD_ALREADY_LOADED, WorldUtils.getShortName(world.getWorldName()));
        }

        WorldUtils.loadBukkitWorld(world.getWorldName());
        world.setLoaded(true);
        world.setWorld(Bukkit.getWorld(world.getWorldName()));
        world.retrieveData();
        loadedWorlds.put(world.getWorldName(), world);
        return world;
    }

    public void unloadWorld(AnaxWorld world) {
        Bukkit.unloadWorld(world.getWorld(), true);
        world.setLoaded(false);
        world.saveData();
        AnaxDatabase.update(world);
        loadedWorlds.remove(world.getWorldName());
    }

    public AnaxWorld createWorld(String worldName) throws LocalizedException {
        String fullPath = Anax.get().getWorldBasePath() + worldName;

        AnaxWorld world = new AnaxWorld();
        world.setWorldName(fullPath);
        world.setDefaults();

        return loadWorld(world);
    }

    public void unloadAll() {
        for(AnaxWorld world : loadedWorlds.values()) {
            unloadWorld(world);
        }
    }

    public AnaxWorld getDefaultWorld() {
        return loadedWorlds.get(Bukkit.getWorlds().get(0).getName());
    }

    public void setupDefaultWorld() {
        String defaultWorldName = Bukkit.getWorlds().get(0).getName();
        AnaxWorld world = AnaxDatabase.getWorld(defaultWorldName);

        if(world == null) {
            world = new AnaxWorld();
            world.setWorld(Bukkit.getWorlds().get(0));
            world.setDefaults();
            world.setLocked(true);
            world.setWorldName(Bukkit.getWorlds().get(0).getName());
            world.addMemeber(RoleType.OWNER, AnaxPlayerManager.getInstance().getServerAsPlayer().getUuid());
        }
        loadedWorlds.put(world.getWorldName(), world);
    }
}
