package io.github.timpcunningham.anax.commands.flags;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.utils.CommandUtils;
import io.github.timpcunningham.anax.utils.Lang;
import io.github.timpcunningham.anax.utils.PlayerUtils;
import io.github.timpcunningham.anax.utils.WorldUtils;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.FlagType;
import io.github.timpcunningham.anax.world.tables.Flag;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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

        boolean result = world.toggleFlag(flag);
        ChatColor resultColor;

        if(result) {
            resultColor = ChatColor.GREEN;
        } else {
            resultColor = ChatColor.RED;
        }

        String flagResult = resultColor + String.valueOf(result);
        sender.sendMessage(Lang.FLAG_VALUE_CHANGED.get(PlayerUtils.getLocale(player), flag.name(), flagResult));
    }

    @Command(
            aliases = "flags",
            desc = "List the flags for a world",
            usage = "[help]",
            min = 0, max = 1
    )
    public static void flags(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        String locale = PlayerUtils.getLocale(player);
        AnaxWorld world = CommandUtils.validateWorldLoaded(sender, player.getWorld().getName());

        if(args.argsLength() == 1 && args.getString(0).toLowerCase().equals("help")) {
            player.sendMessage(Lang.FLAG_HELP.get(locale, "Animals", false, Lang.FLAG_DESC_ANIMALS.get(locale)));
            player.sendMessage(Lang.FLAG_HELP.get(locale, "Explosions", false, Lang.FLAG_DESC_EXPLOSIONS.get(locale)));
            player.sendMessage(Lang.FLAG_HELP.get(locale, "Monsters", false, Lang.FLAG_DESC_MONSTERS.get(locale)));
            player.sendMessage(Lang.FLAG_HELP.get(locale, "Physics", true, Lang.FLAG_DESC_PHYSICS.get(locale)));
            player.sendMessage(Lang.FLAG_HELP.get(locale, "Weather", false, Lang.FLAG_DESC_WEATHER.get(locale)));
        } else if(args.argsLength() == 0) {
            for(Flag flag : world.getFlags().values()) {
                String result = flag.isEnabled() ? ChatColor.GREEN.toString()  : ChatColor.RED.toString();
                result += flag.isEnabled();

                player.sendMessage(ChatColor.DARK_AQUA + flag.getType().name()
                + ChatColor.GRAY + ": " + result);
            }
        } else {
            player.sendMessage(ChatColor.RED + "/flags [help]");
        }
    }

    // ANIMALS(default: true): Toggles passive mob spawning
}
