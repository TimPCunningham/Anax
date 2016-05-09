package io.github.timpcunningham.anax.utils;

import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.world.Access;
import io.github.timpcunningham.anax.world.AnaxWorld;
import io.github.timpcunningham.anax.world.Role;
import org.bukkit.entity.Player;

public class WorldUtils {

    public static void assertCanManage(Player player, AnaxWorld world) throws LocalizedCommandException {
        if(!world.getMemeberList(Role.OWNER).contains(player.getUniqueId())) {
            throw new LocalizedCommandException(player, Lang.WORLD_MANAGE_DENY);
        }
    }

    public static boolean CanBuild(Player player, AnaxWorld world) {
        return  world.getMemeberList(Role.OWNER).contains(player.getUniqueId()) ||
                world.getMemeberList(Role.BUILDER).contains(player.getUniqueId());
    }

    public static boolean CanVisit(Player player, AnaxWorld world) {
        if(world.getAccess().equals(Access.PUBLIC)) {
            return true;
        } else {
            return  world.getMemeberList(Role.OWNER).contains(player.getUniqueId())   ||
                    world.getMemeberList(Role.BUILDER).contains(player.getUniqueId()) ||
                    world.getMemeberList(Role.VISITOR).contains(player.getUniqueId());
        }

    }

    public static String getShortName(String longName) {
        String[] parts = longName.split("/");

        return parts[parts.length - 1];
    }
}
