package io.github.timpcunningham.anax.commands.world;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.github.timpcunningham.anax.events.PermissionChangedEvent;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.player.AnaxPlayer;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.types.RoleType;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MembersCommands {

    @Command(
            aliases = {"add", "addmember"},
            desc = "Adds a player to a world with a specific role",
            usage = "<player> -[bo]",
            flags = "bo",
            min = 1, max = 1
    )
    @CommandPermissions("anax.command.addmember")
    public static void addmember(CommandContext args, CommandSender sender) throws LocalizedException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorld world = CommandUtils.validateWorldLoaded(sender, player.getWorld().getName());
        WorldUtils.assertCanManage(player, world);

        String target_name = args.getString(0);
        AnaxPlayer target = AnaxPlayerManager.getInstance().findAnaxPlayer(target_name);
        RoleType role = RoleType.VISITOR;

        if(target == null) {
            throw new LocalizedCommandException(sender, Lang.PLAYER_NOT_FOUND, target_name);
        }

        if(args.hasFlag('o')) {
            role = RoleType.OWNER;
        } else if(args.hasFlag('b')) {
            role = RoleType.BUILDER;
        }

        if(world.isRole(role, target.getUuid())) {
            throw new LocalizedCommandException(sender, Lang.COMMAND_ADD_ISROLE, target_name, role.name());
        }

        world.addMemeber(role, target.getUuid());
        Chat.alertPlayer(player, Lang.COMMAND_ADD_SUCCESS, null, target.getName(), role.name());

        if(target.getPlayer() != null) {
            Chat.alertPlayer(target.getPlayer(), Lang.COMMAND_ADD_ALERT, Lang.FORMAT_GLOBAL_ALERT, role.name(), world.getShortName());
            if(target.getPlayer().getWorld().getName().equals(world.getFullName())) {
                Bukkit.getServer().getPluginManager().callEvent(new PermissionChangedEvent(player, world));
            }
        }
    }

    @Command(
            aliases = {"removemember"},
            desc = "Removes a player from your world!",
            usage = "<player> -[bo]",
            flags = "bo",
            min = 1, max = 1
    )
    @CommandPermissions("anax.command.removemember")
    public static void removemember(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorld world = CommandUtils.validateWorldLoaded(sender, player.getWorld().getName());
        RoleType roleType = RoleType.VISITOR;
        AnaxPlayer target = AnaxPlayerManager.getInstance().findAnaxPlayer(args.getString(0));

        if(target == null) {
            throw new LocalizedCommandException(sender, Lang.PLAYER_NOT_FOUND, args.getString(0));
        }

        if(args.hasFlag('o')) {
            roleType = RoleType.OWNER;
        } else if(args.hasFlag('b')) {
            roleType = RoleType.BUILDER;
        }

        if(!world.isRole(roleType, target.getUuid())) {
            throw new LocalizedCommandException(sender, Lang.COMMAND_REMOVEMEMBER_NOTROLE, target.getName(), roleType.name());
        }

        world.removeMember(roleType, target.getUuid());
        Chat.alertPlayer(player, Lang.COMMAND_REMOVEMEMBER_SUCCESS, null, target.getName(), roleType.name());

        if(target.getPlayer() != null) {
            Chat.alertPlayer(target.getPlayer(), Lang.COMMAND_REMOVEMEMBER_ALERT, Lang.FORMAT_GLOBAL_ALERT, roleType.name(), world.getShortName());
            if(target.getPlayer().getWorld().getName().equals(world.getFullName())) {
                Bukkit.getServer().getPluginManager().callEvent(new PermissionChangedEvent(player, world));
            }
        }
    }
}
