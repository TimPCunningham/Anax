package io.github.timpcunningham.anax.commands.world;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.utils.*;
import io.github.timpcunningham.anax.world.RoleType;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand {

    @Command(
            aliases = "create",
            desc = "Creates a world",
            usage = "<name>",
            min = 1, max = 1
    )
    @CommandPermissions("anax.command.create")
    public static void create(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        if(AnaxDatabase.isWorld(args.getString(0))) {
            throw new LocalizedCommandException(sender, Lang.WORLD_ALREADY_EXISTS, args.getString(0));
        }

        Player player = CommandUtils.validateAsPlayer(sender);
        //TODO - Add check for worlds created

        try {
            AnaxWorld world = AnaxWorldManagement.getInstance().createWorld(args.getString(0));
            world.addMemeber(RoleType.OWNER, player.getUniqueId());
            AnaxDatabase.save(world);
        } catch (LocalizedException e) {
            player.sendMessage(e.getReason().get(PlayerUtils.getLocale(player)));
            return;
        }

        player.sendMessage(Lang.COMMAND_CREATE_SUCCESS.get(PlayerUtils.getLocale(player)));
    }
}
