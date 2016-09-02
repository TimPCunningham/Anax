package io.github.timpcunningham.anax.commands.flags;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.player.PlayerUtils;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.types.FlagType;
import io.github.timpcunningham.anax.world.tables.Flags;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;


public class ToggleCommand {

    @Command(
            aliases = "toggle",
            desc = "toggles a specific flag for a world",
            usage = "<animals|explosions|monsters|physics|weather>",
            min = 1, max = 1
    )
    @CommandPermissions("anax.command.toggle")
    public static void toggle(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorld world = CommandUtils.validateWorldLoaded(sender, player.getWorld().getName());
        FlagType flag  = CommandUtils.validateFlag(sender, args.getString(0));

        WorldUtils.assertCanManage(player, world);

        if(AnaxWorldManagement.getInstance().isDefaultWorld(world)) {
            throw new LocalizedCommandException(player, Lang.WORLD_DEFAULT_DENY);
        }

        boolean result = world.toggleFlag(flag);
        ChatColor resultColor;

        if(result) {
            resultColor = ChatColor.GREEN;
        } else {
            resultColor = ChatColor.RED;
        }

        String flagResult = resultColor + String.valueOf(result);
        Chat.alertPlayer(player, Lang.FLAG_VALUE_CHANGED, null, flag.name(), flagResult);
    }

    @Command(
            aliases = "flags",
            desc = "List the flags for a world",
            usage = "[help]",
            min = 0, max = 1
    )
    @CommandPermissions("anax.command.flags")
    public static void flags(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorld world = CommandUtils.validateWorldLoaded(sender, player.getWorld().getName());

        if(args.argsLength() == 1 && args.getString(0).toLowerCase().equals("help")) {
            Map<String, Flags.FlagContianer> flags = new Flags().getFlags();
            for(String name : flags.keySet()) {
                String value = String.valueOf(flags.get(name).value);
                String desc = flags.get(name).description.get(PlayerUtils.getLocale(player));

                Chat.alertPlayer(player, Lang.FLAG_HELP, null, name,  value, desc);
            }
        } else if(args.argsLength() == 0) {
            for(String flag : world.getFlags().keySet()) {
                Flags.FlagContianer container = world.getFlags().get(flag);
                String result = container.value ? ChatColor.GREEN.toString()  : ChatColor.RED.toString();
                result += container.value;

                Chat.alertPlayer(player, Lang.FLAG_VALUE, null, flag, result);
            }
        } else {
            Chat.alertPlayer(player, Lang.MESSAGE_DEFAULT, null, "/flags [help]");
        }
    }
}
