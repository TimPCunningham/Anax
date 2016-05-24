package io.github.timpcunningham.anax.listeners;

import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.exceptions.LocalizedPlayerException;
import io.github.timpcunningham.anax.player.AnaxPlayer;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        AnaxPlayer player = AnaxPlayerManager.getInstance().getAnaxPlayer(event.getPlayer().getUniqueId());
        event.setCancelled(true);

        try {
            Chat.PlayerChat(player.getChannel(), player.getPlayer(), event.getMessage());
        } catch (LocalizedPlayerException e) {
            Chat.alertPlayer(player.getPlayer(), e.getReason(), null, e.getArgs());
        }
    }
}