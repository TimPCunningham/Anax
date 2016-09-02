package io.github.timpcunningham.anax.commands;

import com.sk89q.bukkit.util.BukkitWrappedCommandSender;
import com.sk89q.minecraft.util.commands.*;
import io.github.timpcunningham.anax.events.PermissionChangedEvent;
import io.github.timpcunningham.anax.utils.Fuzzy;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.MapPages;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
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
    public static void map(CommandContext args, CommandSender sender) throws  LocalizedException {
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

            if(world.isLoaded() || manager.isDefaultWorld(world)) {
                world = manager.getWorld(world.getFullName());
                player.teleport(world.getSpawn());
            } else {
                if(WorldUtils.CanVisit(player, world)) {
                    manager.loadWorld(world);
                    world.setWorld(Bukkit.getWorld(world.getFullName()));
                    player.teleport(world.getSpawn());
                } else {
                    throw new LocalizedCommandException(player, Lang.WORLD_CANT_ACCESS);
                }
            }

            Chat.alertPlayer(player, Lang.COMMAND_MAP_TELEPORT, null, world.getShortName());
        }
    }

    @Command(
            aliases = {"load"},
            desc = "Loads a world",
            usage = "<world>",
            min = 1, max = 1
    )
    @CommandPermissions("anax.command.load")
    public static void load(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorldManagement manger = AnaxWorldManagement.getInstance();
        String worldName = Fuzzy.findBestMatch(args.getString(0), manger.getCreatedWorldNames());

        if(worldName.equalsIgnoreCase("")) {
            throw new LocalizedCommandException(player, Lang.WORLD_NOT_FOUND, args.getString(0));
        }

        AnaxWorld world = AnaxDatabase.getWorldByShortName(worldName);
        if(world.isLoaded() || manger.isDefaultWorld(world)) {
            throw new LocalizedCommandException(player, Lang.WORLD_ALREADY_LOADED, world.getShortName());
        }

        world.retrieveData();
        WorldUtils.assertCanManage(player, world);

        try {
            manger.loadWorld(world);
            AnaxDatabase.update(world);
            Chat.alertPlayer(player, Lang.WORLD_LOADED, null, world.getShortName());
        } catch (LocalizedException e) {
            throw new LocalizedCommandException(player, e.getReason(), e.getArgs());
        }
    }

    @Command(
            aliases = {"unload"},
            desc = "Unloads a world",
            usage = "<world>",
            min = 1, max = 1
    )
    @CommandPermissions("anax.command.unload")
    public static void unload(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorldManagement manager = AnaxWorldManagement.getInstance();
        String worldName = Fuzzy.findBestMatch(args.getString(0), manager.getWorlds().stream().map(AnaxWorld::getShortName).collect(Collectors.toList()));

        if(worldName.equalsIgnoreCase("")) {
            throw new LocalizedCommandException(player, Lang.WORLD_NOT_FOUND, args.getString(0));
        }

        AnaxWorld world = manager.getWorldByShortName(worldName);
        if(manager.isDefaultWorld(world)) {
            throw new LocalizedCommandException(player, Lang.WORLD_DEFAULT_DENY);
        }

        WorldUtils.assertCanManage(player, world);
        if(!world.isLoaded()) {
            throw new LocalizedCommandException(player, Lang.WORLD_NOT_LOADED, args.getString(0));
        }

        manager.unloadWorld(world);
        Chat.alertPlayer(player, Lang.WORLD_UNLOADED, null, world.getShortName());
    }

    @Command(
            aliases = {"lock"},
            desc = "Locks the current world",
            min = 0, max = 0
    )
    @CommandPermissions("anax.command.lock")
    public static void lock(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        changeLockStatus(sender, true, Lang.COMMAND_LOCK_SUCCESS, Lang.COMMAND_LOCK_ERROR);
    }

    @Command(
            aliases = {"unlock"},
            desc = "Unocks the current world",
            min = 0, max = 0
    )
    @CommandPermissions("anax.command.unlock")
    public static void unlock(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        changeLockStatus(sender, false, Lang.COMMAND_UNLOCK_SUCCESS, Lang.COMMAND_UNLOCK_ERROR);
    }

    private static void changeLockStatus(CommandSender sender, boolean status, Lang success, Lang fail) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorld world = CommandUtils.validateWorldLoaded(sender, player.getWorld().getName());

        WorldUtils.assertCanManage(player, world);

        if(AnaxWorldManagement.getInstance().isDefaultWorld(world)) {
            throw new LocalizedCommandException(player, Lang.WORLD_DEFAULT_DENY);
        }

        if(world.isLocked() == status) {
            throw new LocalizedCommandException(player, fail);
        }

        world.setLocked(status);
        Chat.alertPlayer(player, success, null);

        for(Player inWorld : world.getWorld().getPlayers()) {
            Bukkit.getServer().getPluginManager().callEvent(new PermissionChangedEvent(inWorld, world));
        }
    }
}