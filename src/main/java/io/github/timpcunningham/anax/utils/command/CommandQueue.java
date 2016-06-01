package io.github.timpcunningham.anax.utils.command;

import io.github.timpcunningham.anax.Anax;
import javafx.util.Pair;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class CommandQueue {
    private static CommandQueue self;
    private HashMap<UUID, Pair<Callback, Object[]>> queuedCommands;

    private CommandQueue() {
        queuedCommands = new HashMap<>();
    }

    public static CommandQueue getInstance() {
        if(self == null) {
            self = new CommandQueue();
        }
        return self;
    }

    public void add(UUID sender, int timeout, Callback callback, Object... args) {
        queuedCommands.put(sender, new Pair<>(callback, args));

        new BukkitRunnable() {
            public void run() {
                if(queuedCommands.containsKey(sender)) {
                    queuedCommands.remove(sender);
                }
            }
        } .runTaskLaterAsynchronously(Anax.get(), 20 * timeout);
    }

    public boolean confirm(UUID uuid) {
        if(queuedCommands.containsKey(uuid)) {
            Pair<Callback, Object[]> command = queuedCommands.get(uuid);
            command.getKey().execute(command.getValue());
            queuedCommands.remove(uuid);
            return true;
        }
        return false;
    }
}
