package io.github.timpcunningham.anax.commands.importing;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.commands.downlaod.Dropbox;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.player.CommandUtils;
import io.github.timpcunningham.anax.utils.player.PlayerUtils;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ImportCommand {
    @Command(
            aliases = "import",
            desc = "Import a world",
            usage = "<world> [-n <new name>] [-p <player>]",
            min = 1, max = 3,
            flags = "n:p:"
    )
    @CommandPermissions("anax.command.import")
    public static void importWorld(CommandContext args, CommandSender sender) throws LocalizedCommandException, CommandPermissionsException {
        Player player = CommandUtils.validateAsPlayer(sender);
        String world = args.getString(0);
        String rename = args.hasFlag('n') ? args.getFlag('n') : "";
        String playerName = args.hasFlag('p') ? args.getFlag('p') : player.getName();

        if(args.hasFlag('p') && !sender.hasPermission("anax.command.import.other")) {
            throw new CommandPermissionsException();
        }

        if(!args.hasFlag('p')) {
            int current = PlayerUtils.getCurrentWorldCount(player);
            int max = PlayerUtils.getMaxWorlds(player);

            if(!(current < max)) {
                throw new LocalizedCommandException(player, Lang.COMMAND_CREATE_TOOMANY);
            }
        }

        if(args.hasFlag('p') && !Bukkit.getOfflinePlayer(playerName).hasPlayedBefore()) {
            throw new LocalizedCommandException(player, Lang.PLAYER_NOT_FOUND, playerName);
        }

        rename = rename.equalsIgnoreCase("") ? world : rename;
        UUID owner = args.hasFlag('p') ? Bukkit.getOfflinePlayer(playerName).getUniqueId() : player.getUniqueId();

        final String toName = rename;
        Chat.alertPlayer(player, Lang.COMMAND_IMPORT_WAIT, Lang.FORMAT_GLOBAL_ALERT);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    AnaxWorld imported = Dropbox.getInstance().importWorld(player, owner, world, toName);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            AnaxDatabase.save(imported);

                            try {
                                AnaxWorldManagement.getInstance().loadWorld(imported);
                                Chat.alertPlayer(player, Lang.COMMAND_IMPORT_SUCCESS, Lang.FORMAT_GLOBAL_ALERT);
                            } catch (LocalizedException e) {
                                Chat.alertPlayer(player, e.getReason(), null, e.getArgs());
                            }
                        }
                    }.runTask(Anax.get());
                } catch (LocalizedCommandException e) {
                    Chat.alertPlayer(player, e.getReason(), Lang.FORMAT_GLOBAL_ALERT,  e.getArgs());
                }
            }
        }.runTaskAsynchronously(Anax.get());

    }
}
