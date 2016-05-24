package io.github.timpcunningham.anax.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MapCommands {
    @Command(
            aliases = "maps",
            desc = "Shows a list of maps",
            min = 0, max = 0,
            flags = "a"
    )
    @CommandPermissions("anax.command.maps")
    public static void maps(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        List<AnaxWorld> worlds;

        if(args.hasFlag('a')) {
            worlds = AnaxWorldManagement.getInstance().getWorlds().stream().collect(Collectors.toList());
        } else {
           worlds = AnaxWorldManagement.getInstance().getPlayerWorlds(player.getUniqueId());
        }

        Chat.alertPlayer(player, Lang.COMMAND_MAPS_HEADER, null);
        for(AnaxWorld world : worlds) {
            Chat.alertPlayer(player, Lang.MESSAGE_DEFAULT, null, world.shortInfo());
        }
    }

    @Command(
            aliases = "map",
            desc = "Teleports you to a world",
            usage = "<world>",
            min = 0, max = 1
    )
    @CommandPermissions("anax.command.map")
    public static void map(CommandContext args, CommandSender sender) throws LocalizedCommandException, LocalizedException {
        Player player = CommandUtils.validateAsPlayer(sender);
        List<AnaxWorld> worlds = AnaxWorldManagement.getInstance().getPlayerWorlds(player.getUniqueId());
        AnaxWorld firstLoaded = null;
        AnaxWorldManagement manager = AnaxWorldManagement.getInstance();

        if(worlds.size() > 0)
            firstLoaded = worlds.stream().filter(AnaxWorld::isLoaded).findFirst().get();

        if(firstLoaded != null && args.argsLength() == 0) {
            player.teleport(firstLoaded.getSpawn());
        } else if(args.argsLength() == 0){
            Chat.alertPlayer(player, Lang.SERVER_NOLOADEDMAPS, null);
            Chat.alertPlayer(player, Lang.MESSAGE_DEFAULT, null, "/map <world>");
        } else { //look for loaded map
            AnaxWorld world;
            String worldPath = WorldUtils.getLongName(args.getString(0));

            if(manager.isLoadedWorld(worldPath)) {
                world = manager.getWorld(worldPath);
            } else {
                world = manager.getWorld(worldPath);
                if(world != null) {
                    world.retrieveData();
                }
            }
            if(world == null) {
                throw new LocalizedCommandException(sender, Lang.WORLD_NOT_FOUND, args.getString(0));
            }
            if(!WorldUtils.CanVisit(player, world)) {
                throw new LocalizedCommandException(sender, Lang.WORLD_CANT_ACCESS);
            }
            if(!world.isLoaded()) {
                AnaxWorldManagement.getInstance().loadWorld(world);
            }
            player.teleport(world.getSpawn());
        }
    }
}
