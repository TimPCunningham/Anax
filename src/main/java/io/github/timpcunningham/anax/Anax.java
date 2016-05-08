package io.github.timpcunningham.anax;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import io.github.timpcunningham.anax.world.AnaxWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Anax extends JavaPlugin {
    private static Anax self;
    private CommandsManager<CommandSender> commands;

    @Override
    public void onLoad() {
        self = this;
    }

    @Override
    public void onEnable() {
        setupDatabase();
        setupCommands();
    }

    public static Anax get() {
        return self;
    }

    private void setupCommands() {
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
        };

        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);
    }

    private void setupDatabase() {
        File dbProperties = new File(Bukkit.getWorldContainer(),"eBean.properties");

        try {
            if(!dbProperties.exists()) {
                dbProperties.createNewFile();
            }
            getDatabase().find(AnaxWorld.class).findRowCount();
        } catch (PersistenceException e) {
            getLogger().info("Installing database...");
            installDDL();
        } catch (IOException e) {
            getLogger().info("Problem creating eBean.properties");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + e.getCause().getMessage());
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> dbClazz = new ArrayList<>();

        dbClazz.add(AnaxWorld.class);

        return dbClazz;
    }
}
