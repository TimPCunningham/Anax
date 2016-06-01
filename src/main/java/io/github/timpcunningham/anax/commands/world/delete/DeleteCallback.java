package io.github.timpcunningham.anax.commands.world.delete;

import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.Callback;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.utils.server.Debug;
import io.github.timpcunningham.anax.utils.server.FileUtils;
import io.github.timpcunningham.anax.utils.server.Profiler;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Paths;

public class DeleteCallback implements Callback {
    @Override
    public void execute(Object... args) {
        Profiler profiler = new Profiler("DeleteCallback");
        profiler.start();

        AnaxWorld world = (AnaxWorld)args[0];
        Player player = (Player)args[1];

        AnaxDatabase.deleteWorld(world);
        AnaxPlayerManager.getInstance().sendToHub(world);
        AnaxWorldManagement.getInstance().removeWorld(world.getFullName());

        new BukkitRunnable() {
            public void run() {
                Bukkit.unloadWorld(world.getWorld(), false);
                FileUtils.deleteFile(Paths.get(Bukkit.getWorldContainer().getAbsolutePath(), world.getFullName()).toFile());
                new BukkitRunnable() {
                    public void run() {
                        Chat.alertPlayer(player, Lang.COMMAND_DELETE_SUCCESS, Lang.FORMAT_GLOBAL_ALERT, world.getShortName());
                    }
                }.runTask(Anax.get());
            }
        }.runTaskAsynchronously(Anax.get());

        profiler.end();
        Debug.sender(player, profiler.profile());
    }
}
