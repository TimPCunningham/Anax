package io.github.timpcunningham.anax.commands.confirm;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.CommandQueue;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Confrim {
    @Command(
            aliases = "confirm",
            desc = "Confirms an action",
            min = 0, max = 0
    )
    @CommandPermissions("anax.command.confirm")
    public static void confirm(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);

        boolean queued = CommandQueue.getInstance().confirm(player.getUniqueId());

        if(!queued) {
            throw new LocalizedCommandException(sender, Lang.COMMAND_CONFIRM_NOTFOUND);
        }
    }
}
