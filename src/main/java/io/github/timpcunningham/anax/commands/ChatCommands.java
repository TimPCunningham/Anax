package io.github.timpcunningham.anax.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.player.AnaxPlayer;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import io.github.timpcunningham.anax.player.Channel;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommands {

    @Command(
            aliases = {"g", "global"},
            desc = "Sends a message in the global channel",
            usage = "<message>",
            min = 0, max = -1,
            anyFlags = true
    )
    public static void global(CommandContext args, CommandSender sender) throws LocalizedException {
        handleChat(sender, Channel.GLOBAL, args);
    }

    @Command(
            aliases = {"w", "world"},
            desc = "Sends a message in the world channel",
            usage = "<message>",
            min = 0, max = -1,
            anyFlags = true
    )
    public static void world(CommandContext args, CommandSender sender) throws LocalizedException {
        handleChat(sender, Channel.WORLD, args);
    }

    @Command(
            aliases = {"a", "admin"},
            desc = "Sends a message in the admin channel",
            usage = "<message>",
            min = 0, max = -1,
            anyFlags = true

    )
    public static void admin(CommandContext args, CommandSender sender) throws LocalizedException {
        handleChat(sender, Channel.ADMIN, args);
    }

    private static void handleChat(CommandSender sender, Channel channel, CommandContext args) throws LocalizedException {
        if(sender instanceof Player) {
            Player player = CommandUtils.validateAsPlayer(sender);
            AnaxPlayer anaxPlayer = AnaxPlayerManager.getInstance().getAnaxPlayer(player.getUniqueId());

            if(!player.getPlayer().hasPermission(channel.getPermission())) {
                throw new LocalizedCommandException(player.getPlayer(), Lang.CHAT_CHANNEL_SET_DENIED, channel.name());
            }

            if(args.argsLength() == 0) {
                if(anaxPlayer.getChannel().equals(channel)) {
                    throw new LocalizedCommandException(player.getPlayer(), Lang.CHAT_CHANNEL_DUPLICATE, channel.name());
                }
                anaxPlayer.setChannel(channel);
                Chat.alertPlayer(player.getPlayer(), Lang.CHAT_CHANNEL_SET, null, channel.name());
            } else {
                Chat.PlayerChat(channel, player.getPlayer(), args.getJoinedStrings(0));
            }
        } else {
            if(args.argsLength() > 0) {
                Chat.ConsoleChat(Channel.ADMIN, args.getJoinedStrings(0));
            } else {
                throw new LocalizedCommandException(sender, Lang.CHAT_CHANNEL_SET_DENIED, Channel.ADMIN.name());
            }
        }
    }
}
