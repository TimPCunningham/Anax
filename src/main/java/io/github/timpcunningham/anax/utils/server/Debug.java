package io.github.timpcunningham.anax.utils.server;

import io.github.timpcunningham.anax.Anax;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Debug {
    private static final String DEBUG_HEADER = ChatColor.GRAY + "[" + ChatColor.DARK_AQUA  + "DEBUG" + ChatColor.GRAY + "] ";

    public static void all(String message) {
        Bukkit.broadcastMessage(DEBUG_HEADER + caller() + message);
    }

    public static void sender(CommandSender sender, String message) {
       sender.sendMessage(DEBUG_HEADER + caller() + message);
    }

    public static void simple(String message) {
        Bukkit.broadcastMessage( ChatColor.GRAY + message);
    }

    public static void warning(String message) {
        Anax.get().getLogger().warning(DEBUG_HEADER + caller() + message);
    }

    public static void info(String message) {
        Anax.get().getLogger().info(DEBUG_HEADER + caller() + message);
    }

    public static void severe(String message) {
        Anax.get().getLogger().severe(DEBUG_HEADER + caller() + message);
    }

    public static void ops(String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.isOp()) {
                player.sendMessage(DEBUG_HEADER + caller() + message);
            }
        }
    }

    private static String caller() {
        StackTraceElement[] element = Thread.currentThread().getStackTrace();
        String[] clazzes = element[3].getClassName().split("\\.");
        String clazz = clazzes[clazzes.length - 1];
        int line = element[3].getLineNumber();
        return clazz + ".java:" + line + ": ";
    }
}
