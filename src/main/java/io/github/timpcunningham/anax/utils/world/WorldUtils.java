package io.github.timpcunningham.anax.utils.world;

import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.world.Access;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.RoleType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class WorldUtils {

    public static void assertCanManage(Player player, AnaxWorld world) throws LocalizedCommandException {
        if(!world.isRole(RoleType.OWNER, player.getUniqueId())) {
            throw new LocalizedCommandException(player, Lang.WORLD_MANAGE_DENY);
        }
    }

    public static boolean CanBuild(Player player, AnaxWorld world) {
        UUID uuid = player.getUniqueId();
        return  world.isRole(RoleType.OWNER, uuid) ||
                world.isRole(RoleType.BUILDER, uuid);
    }

    public static boolean CanVisit(Player player, AnaxWorld world) {
        UUID uuid = player.getUniqueId();
        if(world.getAccess().equals(Access.PUBLIC)) {
            return true;
        } else {
            return  world.isRole(RoleType.OWNER, uuid)  ||
                    world.isRole(RoleType.BUILDER, uuid) ||
                    world.isRole(RoleType.VISITOR, uuid);
        }
    }

    public static boolean isBukkitWorldLoaded(final String worldName) {
        return Bukkit.getWorlds().stream().filter(world -> world.getName().equals(worldName)).count() > 0;
    }

    public static World loadBukkitWorld(String worldName) {
        WorldCreator creator = new WorldCreator(worldName);
        return creator.generator(new voidGenerator())
                .generateStructures(false)
                .type(WorldType.FLAT)
                .createWorld();
    }

    public static void  loadAllWorlds() {
        List<AnaxWorld> worlds = Anax.get().getDatabase().find(AnaxWorld.class).where().eq("loaded", true).findList();
        AnaxWorldManagement management = AnaxWorldManagement.getInstance();

        for(AnaxWorld world : worlds) {
            try {
                management.loadWorld(world);
                Anax.get().getLogger().info(Lang.SERVER_WORLD_LOADED.get("en_US", world.getShortName()));
            } catch (LocalizedException  e) {
                Anax.get().getLogger().severe(Lang.SERVER_WORLD_LOAD_FAILED.get("en_US", world.getShortName()));
            }
        }
        management.setupDefaultWorld();
    }

    public static String uuidsToListing(List<UUID> names) {
        String result = "";
        AnaxPlayerManager manager = AnaxPlayerManager.getInstance();

        for(int index = 0; index < names.size(); index++) {
            String name = manager.getName(names.get(index));
            if(index == (names.size()-1) && names.size() > 1) {
                result += "and " + name; //Change to name lookup eventually
            } else if(names.size() == 1){
                result = name;
            } else {
                result += name + ", ";
            }
        }
        return result;
    }
}
