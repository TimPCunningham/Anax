package io.github.timpcunningham.anax.commands.flags;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.utils.CommandUtils;
import io.github.timpcunningham.anax.utils.Lang;
import io.github.timpcunningham.anax.utils.WorldUtils;
import io.github.timpcunningham.anax.world.AnaxWorld;
import io.github.timpcunningham.anax.world.Flag;
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
    public static void toggle(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorld world = CommandUtils.validateWorldLoaded(sender, player.getWorld().getName());
        Flag flag  = CommandUtils.validateFlag(sender, args.getString(0));

        WorldUtils.assertCanManage(player, world);

        boolean result = world.toggleFlag(flag);
        ChatColor resultColor;

        if(result) {
            resultColor = ChatColor.GREEN;
        } else {
            resultColor = ChatColor.RED;
        }

        sender.sendMessage(Lang.FLAG_VALUE_CHANGED.get(player.spigot().getLocale(), flag.name(), resultColor + String.valueOf(result)));
    }
}
