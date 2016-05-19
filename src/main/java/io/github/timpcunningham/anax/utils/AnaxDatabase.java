package io.github.timpcunningham.anax.utils;

import com.avaje.ebean.Query;
import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.RoleType;

import java.util.ArrayList;
import java.util.List;
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

    public static AnaxWorld getWorld(String worldName) {
        return Anax.get().getDatabase().find(AnaxWorld.class).where().eq("worldName", worldName).findUnique();
    }


    public static List<AnaxWorld> getLoadedWorlds() {
        return Anax.get().getDatabase().find(AnaxWorld.class).where().eq("loaded", true).findList();
    }

    public static boolean isWorld(String worldName) {
        return Anax.get().getDatabase().find(AnaxWorld.class).where().eq("worldName", worldName).findRowCount() > 0;
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

    public static void delete(Object obj) {
        Anax.get().getDatabase().delete(obj);
    }
}
