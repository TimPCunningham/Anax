package io.github.timpcunningham.anax.commands.world;

import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.player.AnaxPlayer;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import io.github.timpcunningham.anax.utils.Fuzzy;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.ComponentBuilder;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.StringUtil;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import io.github.timpcunningham.anax.utils.player.PlayerUtils;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.tables.Flags;
import io.github.timpcunningham.anax.world.types.RoleType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class MapInfoCommand {

    @Command(
            aliases = {"mapinfo", "worldinfo", "mi",  "wi"},
            desc = "Shows information of a world",
            usage = "[world]",
            min = 0, max = 1
    )
    public static void mapinfo(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        ComponentBuilder mapinfo = new ComponentBuilder();
        String header;
        String locale = "en_US";
        String status;
        AnaxWorld world;
        AnaxWorldManagement worldManager = AnaxWorldManagement.getInstance();

        if(args.argsLength() == 1) {
            List<String> worlds = worldManager.getCreatedWorldNames();
            String match = Fuzzy.findBestMatch(args.getString(0), worlds);

            if(match.equals("")) {
                throw new LocalizedCommandException(sender, Lang.WORLD_NOT_FOUND, args.getString(0));
            }
            world = AnaxDatabase.getWorldByShortName(match);
            if(worldManager.isLoadedWorld(world.getFullName())) {
                world = worldManager.getWorld(world.getFullName());
            } else {
                world.retrieveData();
            }
        } else {
            Player player = CommandUtils.validateAsPlayer(sender);
            world = CommandUtils.validateWorldLoaded(player, player.getWorld().getName());
            locale = PlayerUtils.getLocale(player);
        }

        header = StringUtil.center(ChatColor.YELLOW + Lang.COMMAND_MAPINFO_HEADER.get(locale, world.getShortName()), '-', ChatColor.GRAY);
        mapinfo.text(header);
        mapinfo.add(new TextComponent("\n\n"));
        mapinfo.add(getGroupList(RoleType.OWNER, world).build()).add(new TextComponent(" "));
        mapinfo.add(getGroupList(RoleType.BUILDER, world).build()).add(new TextComponent(" "));
        mapinfo.add(getGroupList(RoleType.VISITOR, world).build()).add(new TextComponent("\n\n"));
        status = world.isLocked() ? ChatColor.RED + "LOCKED" : ChatColor.GREEN + "UNLOCKED";
        mapinfo.add(new ComponentBuilder().text(locale, Lang.COMMAND_MAPINFO_SPECIAL, "Status", status + "\n").build());
        mapinfo.add(new ComponentBuilder().text(locale, Lang.COMMAND_MAPINFO_SPECIAL, "Phase", world.getPhase().name() + "\n").build());
        mapinfo.add(new ComponentBuilder().text(locale, Lang.COMMAND_MAPINFO_SPECIAL, "Visibility", world.getAccess().name() + "\n").build());
        mapinfo.add(new ComponentBuilder().text(locale, Lang.COMMAND_MAPINFO_SPECIAL, "Spawn", world.getSpawn().toVector() + "\n\n").build());
        mapinfo.add(getFlagStatus(world).build());

        if(sender instanceof Player) {
            Chat.alertPlayer((Player) sender, mapinfo.build());
        } else {
            Chat.alertConsole(mapinfo.build().getText());
        }
    }

    private static ComponentBuilder getGroupList(RoleType roleType, AnaxWorld world) {
        ComponentBuilder builder = new ComponentBuilder();
        AnaxPlayerManager playerManager = AnaxPlayerManager.getInstance();
        List<TextComponent> memberComponents = new ArrayList<>();

        builder.text(ChatColor.GRAY + "[" + ChatColor.YELLOW + roleType.name() + ChatColor.GRAY + "]");
        Set<UUID> members = world.getMemeberList(roleType);
        memberComponents.add(new TextComponent(ChatColor.YELLOW + roleType.name() + "S" + ChatColor.GRAY + ":"));

        for(UUID uuid : members) {
            AnaxPlayer anaxPlayer = playerManager.getAnaxPlayer(uuid);
            memberComponents.add(new ComponentBuilder().text(ChatColor.GRAY + "\n " + ChatColor.GREEN + anaxPlayer.getName()).build());
        }

        builder.hover(memberComponents.toArray(new TextComponent[memberComponents.size()]));
        return builder;
    }

    private static ComponentBuilder getFlagStatus(AnaxWorld world) {
        ComponentBuilder builder = new ComponentBuilder();
        List<TextComponent> flagComponents = new ArrayList<>();

        builder.text(ChatColor.GRAY + "[" + ChatColor.YELLOW + "FLAGS" + ChatColor.GRAY + "]");

        flagComponents.add(new TextComponent(ChatColor.YELLOW + "FLAGS" + ChatColor.GRAY + ":"));
        for(Map.Entry flag : world.getFlags().entrySet()) {
            Flags.FlagContianer flagContainer = (Flags.FlagContianer)flag.getValue();
            flagComponents.add(new TextComponent(ChatColor.GRAY + "\n " + flagContainer.formatedName));
        }

        return builder.hover(flagComponents.toArray(new TextComponent[flagComponents.size()]));
    }
}
