package io.github.timpcunningham.anax.utils.player;

import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.player.AnaxPlayer;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.tables.Role;
import io.github.timpcunningham.anax.world.types.RoleType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerUtils {

    public static String getLocale(Player player) {
        return player.spigot().getLocale();
    }

    public static UUID getUUID(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);

        if(player.hasPlayedBefore()) {
            return player.getUniqueId();
        } else {
            //TODO - Lookup https://api.mojang.com/users/profiles/minecraft/<Username>
            return null;
        }
    }

    public static String getName(UUID uuid) {
        AnaxPlayer player = AnaxDatabase.getAnaxPlayer(uuid);

        if(player != null) {
            return player.getName();
        } else {
            //TODO - Lookup
            return "N/A";
        }
    }

    public static List<String> toNameList(List<UUID> names) {
        List<String> result = new ArrayList<>();

        names.forEach(uuid -> result.add(getName(uuid)));

        return result;
    }

    public static int getCurrentWorldCount(Player player) {
        UUID uuid = player.getUniqueId();

        return (int) AnaxDatabase.getAnaxWorlds().stream()
                .filter(world -> world.getFullName().contains(String.valueOf(uuid)))
                .count();
    }

    public static int getMaxWorlds(Player player) {
        if(player.hasPermission("anax.world.unlimited")) {
            return Integer.MAX_VALUE;
        } else {
            for(PermissionAttachmentInfo info : player.getEffectivePermissions()) {
                if(info.getPermission().startsWith("anax.world.")) {
                    String[] parts = info.getPermission().split("anax.world.");
                    try {
                        return Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        Chat.alertConsole(Lang.PLAYER_WORLD_LIMIT_PARSE_ERROR, player.getName());
                    }
                }
            }
        }
        return 1;
    }

    public static boolean hasRole(Player player, AnaxWorld world, RoleType type) {
        Set<UUID> members = world.getMemeberList(type);

        return members.contains(player.getUniqueId());
    }
}
