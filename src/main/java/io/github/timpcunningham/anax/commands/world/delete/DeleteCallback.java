package io.github.timpcunningham.anax.commands.world.delete;

import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.listeners.UnloadListener;
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
        AnaxWorld world = (AnaxWorld)args[0];
        Player player = (Player)args[1];

        AnaxDatabase.deleteWorld(world);
        if(world.isLoaded()) {
            AnaxPlayerManager.getInstance().sendToHub(world);
            AnaxWorldManagement.getInstance().removeWorld(world.getFullName());
        }

        if(UnloadListener.getInstance().isEmpty(world.getFullName())) {
            UnloadListener.getInstance().remove(world.getFullName());
        }

        new BukkitRunnable() {
            public void run() {
                if(world.isLoaded()) {
                    Bukkit.unloadWorld(world.getWorld(), false);
                }
                System.gc();
                FileUtils.deleteFile(Paths.get(Bukkit.getWorldContainer().getAbsolutePath(), world.getFullName()).toFile());
                Chat.alertPlayer(player, Lang.COMMAND_DELETE_SUCCESS, Lang.FORMAT_GLOBAL_ALERT, world.getShortName());
            }
        }.runTaskAsynchronously(Anax.get());
    }
}
