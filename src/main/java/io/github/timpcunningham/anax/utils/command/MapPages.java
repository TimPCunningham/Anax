package io.github.timpcunningham.anax.utils.command;

import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandSender;
import com.sk89q.minecraft.util.pagination.PaginatedResult;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.player.PlayerUtils;
import io.github.timpcunningham.anax.world.types.RoleType;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class MapPages extends PaginatedResult<AnaxWorld> {
    private String locale;

    public MapPages(CommandSender sender) {
        super(8);
        if(sender instanceof Player) {
            this.locale = PlayerUtils.getLocale((Player) sender);
        } else {
            this.locale = "en_US";
        }
    }

    @Override
    public void display(WrappedCommandSender sender, List<? extends AnaxWorld> result, int page) throws CommandException {
        Collections.sort(result);
        super.display(sender, result, page);
    }

    @Override
    public String formatHeader(int page, int max) {
        String header;

        header = Lang.COMMAND_MAPS_HEADER.get(locale, page, max);

        return StringUtil.center(header, '=', ChatColor.RED);
    }

    @Override
    public String format(AnaxWorld world, int i) {
        List<UUID> owners = new ArrayList<>(world.getMemeberList(RoleType.OWNER));

        if(owners.size() > 0) {
            return Lang.COMMAND_MAPS_MULTIOWNERS.get(locale, (i + 1), world.getShortName(), StringUtil.toDisplay(PlayerUtils.toNameList(owners)));
        }
        return Lang.COMMAND_MAPS_NOOWNERS.get(locale, (i + 1), world.getShortName());
    }
}
