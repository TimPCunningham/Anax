package io.github.timpcunningham.anax.commands;

import com.sk89q.bukkit.util.BukkitWrappedCommandSender;
import com.sk89q.minecraft.util.commands.*;
import io.github.timpcunningham.anax.utils.Fuzzy;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.MapPages;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.utils.server.Debug;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapCommands {
    @Command(
            aliases = "maps",
            desc = "Shows a list of maps",
            min = 0, max = 1,
            flags = "m"
    )
    @CommandPermissions("anax.command.maps")
    public static void maps(CommandContext args, CommandSender sender) throws LocalizedCommandException, CommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        List<AnaxWorld> worlds;
        int page = 1;

        if(args.argsLength() == 0) {
            page = args.getInteger(0, 1);
        }

        if(args.hasFlag('m')) {
            worlds = AnaxWorldManagement.getInstance().getPlayerWorlds(player.getUniqueId());
        } else {
            worlds = new ArrayList<>(AnaxWorldManagement.getInstance().getWorlds());
        }

        if(worlds.size() > 0) {
            MapPages pages = new MapPages(sender);
            WrappedCommandSender wrappedSender = new BukkitWrappedCommandSender(sender);

            pages.display(wrappedSender, worlds, page);
        } else {
            throw new LocalizedCommandException(sender, Lang.COMMAND_MAPS_NOWORLDS);
        }
    }

    @Command(
            aliases = "map",
            desc = "Teleports you to a world",
            usage = "<world>",
            min = 0, max = 1
    )
    @CommandPermissions("anax.tp")
    public static void map(CommandContext args, CommandSender sender) throws LocalizedCommandException, LocalizedException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorldManagement manager = AnaxWorldManagement.getInstance();

        if(args.argsLength() == 0) {
            List<AnaxWorld> worlds = AnaxWorldManagement.getInstance().getPlayerWorlds(player.getUniqueId());
            if(worlds.size() > 0) {
                AnaxWorld world = worlds.get(0);
                player.teleport(world.getSpawn());
                Chat.alertPlayer(player, Lang.COMMAND_MAP_TELEPORT, null, world.getShortName());
            } else {
                throw new LocalizedCommandException(player, Lang.SERVER_NOLOADEDMAPS);
            }
        } else {
            List<String> worlds = manager.getCreatedWorldNames();
            String match = Fuzzy.findBestMatch(args.getString(0), worlds);

            if(match.equalsIgnoreCase("")) {
                throw new LocalizedCommandException(player, Lang.WORLD_NOT_FOUND, args.getString(0));
            }

            AnaxWorld world = AnaxDatabase.getWorldByShortName(match);
            world.setWorld(Bukkit.getWorld(world.getFullName()));

            if(world.isLoaded() || manager.isDefaultWorld(world)) {
                world = manager.getWorld(world.getFullName());
                player.teleport(world.getSpawn());
            } else {
                if(WorldUtils.CanVisit(player, world)) {
                    manager.loadWorld(world);
                    player.teleport(world.getSpawn());
                } else {
                    throw new LocalizedCommandException(player, Lang.WORLD_CANT_ACCESS);
                }
            }
            Chat.alertPlayer(player, Lang.COMMAND_MAP_TELEPORT, null, world.getShortName());
        }
    }
}
