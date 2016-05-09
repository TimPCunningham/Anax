package io.github.timpcunningham.anax.exceptions;

import io.github.timpcunningham.anax.utils.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalizedCommandException extends Exception {
    private CommandSender sender;
    private Lang message;
    private Object[] args;

    public LocalizedCommandException(CommandSender sender, Lang message, Object... args) {
        this.sender = sender;
        this.message = message;
        this.args = args;
    }

    @Override
    public String getMessage() {
        if(sender instanceof Player) {
            String locale = ((Player) sender).spigot().getLocale();
            return message.get(locale, args);
        }
        return message.get("en_US", args);
    }
}
