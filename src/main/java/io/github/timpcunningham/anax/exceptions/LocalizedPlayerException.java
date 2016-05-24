package io.github.timpcunningham.anax.exceptions;

import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.player.PlayerUtils;
import org.bukkit.entity.Player;

public class LocalizedPlayerException extends LocalizedException {
    Player player;

    public LocalizedPlayerException(Player player, Lang message, Object... args) {
        super(message, args);
        this.player = player;
    }

    @Override
    public String getMessage() {
        String locale = PlayerUtils.getLocale(player);
        return getReason().get(locale, getArgs());
    }
}
