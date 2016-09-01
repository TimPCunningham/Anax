package io.github.timpcunningham.anax.utils.server;

import com.avaje.ebean.Query;
import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.player.AnaxPlayer;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.RoleType;
import io.github.timpcunningham.anax.world.tables.Flags;
import io.github.timpcunningham.anax.world.tables.Spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AnaxDatabase {

    public static List<AnaxWorld> getAnaxWorlds() {
        return Anax.get().getDatabase().find(AnaxWorld.class).findList();
    }

    public static List<AnaxWorld> getPlayerWorlds(UUID uuid) {
        try {
            return getAnaxWorlds().stream()
                    .filter(world -> world.getMemeberList(RoleType.OWNER).contains(uuid))
                    .collect(Collectors.toList());
        } catch (NullPointerException npe) {
            return new ArrayList<>();
        }
    }

    public static AnaxWorld getWorldByShortName(String worldName) {
        List<AnaxWorld> worlds = AnaxDatabase.getAnaxWorlds();

        return worlds.stream()
                .filter(world -> world.getShortName().equalsIgnoreCase(worldName.toLowerCase()))
                .findFirst().get();
    }

    public static AnaxWorld getWorldByFullName(String worldName) {
        return Anax.get().getDatabase().find(AnaxWorld.class).where().eq("fullName", worldName).findUnique();
    }


    public static List<AnaxWorld> getLoadedWorlds() {
        return Anax.get().getDatabase().find(AnaxWorld.class).where().eq("loaded", true).findList();
    }

    public static boolean isWorld(String worldName) {

        return Anax.get().getDatabase().find(AnaxWorld.class).findList()
                .stream().filter(world -> world.getShortName().equalsIgnoreCase(worldName))
                .count() > 0;
    }

    public static AnaxPlayer getAnaxPlayer(UUID uuid) {
        return Anax.get().getDatabase().find(AnaxPlayer.class).where().eq("uuid", uuid).findUnique();
    }

    public static void save(Object obj) {
        Anax.get().getDatabase().save(obj);
    }

    public static void update(Object obj) {
        Anax.get().getDatabase().update(obj);
    }

    public static Query find(Class clazz) {
        return Anax.get().getDatabase().find(clazz);
    }

    public static void delete(Object object) {
        Anax.get().getDatabase().delete(object);
    }

    public static void deleteWorld(AnaxWorld world) {
        Spawn spawn = (Spawn)find(Spawn.class).where().eq("worldName", world.getFullName()).findUnique();
        Flags flags = (Flags)find(Flags.class).where().eq("worldName", world.getFullName()).findUnique();

        for(RoleType role : RoleType.values()) {
            Set<UUID> members = world.getMemeberList(role);
            for (UUID uuid : members) {
                world.removeMember(role, uuid);
            }
        }

        delete(spawn);
        delete(flags);
        delete(world);
    }

}
