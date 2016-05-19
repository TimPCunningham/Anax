package io.github.timpcunningham.anax.utils;

import com.github.rmsy.channels.Channel;
import com.github.rmsy.channels.impl.SimpleChannel;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class ChatUtils {

    public static Channel getSimpleWorldChannel() {
        String message = ChatColor.AQUA + "[world] {1}" + ChatColor.WHITE + ": {2}";
        String broadcast = ChatColor.AQUA + "[world] {2}";
        Permission permission = new Permission("anax.chat.receive", PermissionDefault.FALSE);
        return new SimpleChannel(message, broadcast, permission);
    }
}
