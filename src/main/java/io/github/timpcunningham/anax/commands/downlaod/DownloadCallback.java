package io.github.timpcunningham.anax.commands.downlaod;

import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.ComponentBuilder;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.Callback;
import io.github.timpcunningham.anax.utils.player.PlayerUtils;
import io.github.timpcunningham.anax.utils.server.FileUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

import java.io.File;

public class DownloadCallback implements Callback {

    @Override
    public void execute(Object... args) {
        Player player = (Player) args[0];
        String url = String.valueOf(args[1]);
        String path = String.valueOf(args[2]);
        String locale = PlayerUtils.getLocale(player);

        FileUtils.deleteFile(new File(path));

        ComponentBuilder hover = new ComponentBuilder();
        ComponentBuilder builder = new ComponentBuilder();

        hover.text(locale, Lang.COMMAND_DOWNLOAD_URL_HOVER).color(ChatColor.YELLOW);
        builder.text(locale, Lang.COMMAND_DOWNLOAD_URL)
                .color(ChatColor.GREEN)
                .click(ClickEvent.Action.OPEN_URL, url)
                .hover(hover.build());

        Chat.alertPlayer(player, builder.build());
    }
}
