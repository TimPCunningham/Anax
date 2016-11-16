package io.github.timpcunningham.anax.exceptions;

import io.github.timpcunningham.anax.utils.chat.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalizedCommandException extends LocalizedException {
    private CommandSender sender;

    public LocalizedCommandException(CommandSender sender, Lang message, Object... args) {
        super(message, args);
        this.sender = sender;
    }

    @Override
    public String getMessage() {
        if(sender instanceof Player) {
            String locale = ((Player) sender).spigot().getLocale();
            return getReason().get(locale, getArgs());
        }
        return super.getMessage();
    }
}
