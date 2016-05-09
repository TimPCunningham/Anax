package io.github.timpcunningham.anax.utils;

import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.world.AnaxWorld;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.Flag;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUtils {

    /**
     * Validates the command sender is a player
     *
     * @param sender The person who to be validated as a player
     * @return Returns player if validation is passed
     * @throws LocalizedCommandException Thrown if sender is not a player
     */
    public static Player validateAsPlayer(CommandSender sender) throws LocalizedCommandException {
        if(!(sender instanceof Player)) {
            throw new LocalizedCommandException(sender, Lang.COMMAND_ERROR_SENDER_NOT_PLAYER);
        }
        return (Player) sender;
    }

    /**
     * Validates that a world is loaded
     *
     * @param sender The person who sent this command
     * @param worldName The world that needs to be fetched
     * @return Returns the loaded Anax world
     * @throws LocalizedCommandException Thrown if the world is not loaded
     */
    public static AnaxWorld validateWorldLoaded(CommandSender sender, String worldName) throws LocalizedCommandException {
        AnaxWorldManagement instance = AnaxWorldManagement.getInstance();

        if(!instance.isLoadedWorld(worldName)) {
            throw new LocalizedCommandException(sender, Lang.WORLD_NOT_LOADED, worldName);
        }

        return instance.getWorld(worldName);
    }

    public static Flag validateFlag(CommandSender sender, String flagName) throws LocalizedCommandException {
        try {
            return Flag.valueOf(flagName.toUpperCase());
        } catch (Exception e) {
            throw new LocalizedCommandException(sender, Lang.FLAG_NOT_FOUND, flagName);
        }
    }
}
