package io.github.timpcunningham.anax.commands.world.delete;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.utils.Fuzzy;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.CommandQueue;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand {
    @Command(
            aliases = "delete",
            desc = "Deletes a world!",
            min = 0, max = 1
    )
    @CommandPermissions("anax.command.delete")
    public static void delete(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorldManagement worldmanager = AnaxWorldManagement.getInstance();
        AnaxWorld world;
        String worldName;

        if(args.argsLength() == 1) {
            try {
                worldName = AnaxDatabase.getWorldByShortName(args.getString(0)).getFullName();
            } catch (Exception e) {
                throw new LocalizedCommandException(sender, Lang.WORLD_NOT_FOUND, args.getString(0));
            }
        } else {
            worldName = player.getWorld().getName();
        }

        if(worldmanager.isLoadedWorld(worldName)) {
            world = worldmanager.getWorld(worldName);
        } else {
            world = AnaxDatabase.getWorldByFullName(worldName);
            world.retrieveData();
        }

        WorldUtils.assertCanManage(player, world);

        if(AnaxWorldManagement.getInstance().isDefaultWorld(world)) {
            throw new LocalizedCommandException(player, Lang.WORLD_DEFAULT_DENY);
        }

        Chat.alertPlayer(player, Lang.COMMAND_DELETE_ALERT, Lang.FORMAT_GLOBAL_ALERT, world.getShortName());
        Chat.alertPlayer(player, Lang.COMMAND_CONFIRM, Lang.FORMAT_GLOBAL_ALERT, 15);
        CommandQueue.getInstance().add(player.getUniqueId(), 15, new DeleteCallback(), world, player);
    }
}
