package io.github.timpcunningham.anax.utils.chat;

import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.exceptions.LocalizedPlayerException;
import io.github.timpcunningham.anax.player.Channel;
import io.github.timpcunningham.anax.utils.player.PlayerUtils;
import io.github.timpcunningham.anax.utils.server.Debug;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Chat {

    public static void alertPlayer(Player player, Lang message, Lang alertFormat, Object... args) {
        String locale = PlayerUtils.getLocale(player);
        String alert = message.get(locale, args);

        if(alertFormat != null) {
            alert = alertFormat.get(locale, alert);
        }
        player.sendMessage(alert);
    }

    public static void alertWorld(World world, Lang message, Object... args) {
        for(Player player : getReceivingList(Channel.WORLD, world)) {
            alertPlayer(player, message, Lang.FORMAT_WORLD_ALERT, args);
        }
    }

    public static void alertAdmin(Lang message, Object... args) {
        for(Player player : getReceivingList(Channel.ADMIN, null)) {
            alertPlayer(player, message, Lang.FORMAT_ADMIN_ALERT, args);
        }
        alertConsole(message, args);
    }

    public static void alertConsole(Lang message, Object... args) {
        alertConsole(message.get("en_US", args));
    }

    public static void alertConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void PlayerChat(Channel channel, Player player, String message) throws LocalizedPlayerException {
       if(!player.hasPermission(channel.getPermission())) {
           throw new LocalizedPlayerException(player, Lang.CHAT_PERMISSION_DENIED, channel.name());
       }

        for(Player receiver : getReceivingList(channel, player.getWorld())) {
            alertPlayer(receiver, Lang.MESSAGE_DEFAULT, null, channel.format(player.getName(), message));
        }

        alertConsole(channel.format(player.getName(), message));
    }

    public static void ConsoleChat(Channel channel, String message) throws LocalizedException {
        if(channel.equals(Channel.WORLD)) {
            throw new LocalizedException(Lang.CHAT_PERMISSION_DENIED, channel.name());
        }

        for(Player player : getReceivingList(channel, null)) {
            alertPlayer(player, Lang.MESSAGE_CONSOLE, null, message);
        }
    }

    private static List<Player> getReceivingList(Channel channel, World world) {
        switch (channel) {
            case ADMIN:
            case GLOBAL:
                return Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.hasPermission(channel.getPermission()))
                        .collect(Collectors.toList());
            case WORLD:
                return world.getPlayers().stream()
                        .filter(player -> player.hasPermission(channel.getPermission()))
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }
}
