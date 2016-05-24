package io.github.timpcunningham.anax.utils.player;

import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.FlagType;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.World;

public class EventUtils {

    public static boolean event(FlagType type, World world) {
        AnaxWorld anaxWorld = AnaxWorldManagement.getInstance().getWorld(world.getName());

        if(anaxWorld.isLocked()) {
            return true;
        }

        return !anaxWorld.getFlagValue(type);
    }
}
