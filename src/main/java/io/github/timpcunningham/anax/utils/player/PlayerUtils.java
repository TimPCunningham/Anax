package io.github.timpcunningham.anax.utils.player;

import org.bukkit.entity.Player;

public class PlayerUtils {

    public static String getLocale(Player player) {
        return player.spigot().getLocale();
    }
}