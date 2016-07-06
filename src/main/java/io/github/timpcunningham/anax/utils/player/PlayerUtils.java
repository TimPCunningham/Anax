package io.github.timpcunningham.anax.utils.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(player.hasPlayedBefore()) {
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
}
