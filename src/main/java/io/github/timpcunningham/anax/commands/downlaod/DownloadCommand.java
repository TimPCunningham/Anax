package io.github.timpcunningham.anax.commands.downlaod;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.ComponentBuilder;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.Callback;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import io.github.timpcunningham.anax.utils.player.PlayerUtils;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Paths;

public class DownloadCommand {

    @Command(
            aliases = {"download", "dl", "mapdl", "worlddl"},
            desc = "Gets a download of the current world",
            min = 0, max = 0,
            flags = "p"
    )
    @CommandPermissions("anax.command.download")
    public static void download(CommandContext args, CommandSender sender) throws LocalizedCommandException {
        Player player = CommandUtils.validateAsPlayer(sender);
        AnaxWorld world = CommandUtils.validateWorldLoaded(sender, player.getWorld().getName());

        WorldUtils.assertCanManage(player, world);
        world.getWorld().save();
        Chat.alertPlayer(player, Lang.COMMAND_DOWNLOAD_WAIT, Lang.FORMAT_GLOBAL_ALERT);

        new BukkitRunnable() {
            public void run() {
                try {
                    String path = Paths.get(Bukkit.getWorldContainer().getAbsolutePath(), world.getFullName()).toFile().getAbsolutePath();
                    String zipLocation = Dropbox.getInstance().zip(path);
                    Dropbox.getInstance().upload(player, zipLocation, world.getShortName(), new DownloadCallback());
                } catch (LocalizedException e) {
                    Chat.alertPlayer(player, e.getReason(), null, e.getArgs());
                }
            }
        }.runTaskAsynchronously(Anax.get());
    }
}
