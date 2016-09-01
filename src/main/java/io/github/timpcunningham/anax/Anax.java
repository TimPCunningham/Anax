package io.github.timpcunningham.anax;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import io.github.timpcunningham.anax.commands.ChatCommands;
import io.github.timpcunningham.anax.commands.MapCommands;
import io.github.timpcunningham.anax.commands.confirm.Confrim;
import io.github.timpcunningham.anax.commands.downlaod.DownloadCommand;
import io.github.timpcunningham.anax.commands.downlaod.Dropbox;
import io.github.timpcunningham.anax.commands.flags.ToggleCommand;
import io.github.timpcunningham.anax.commands.importing.ImportCommand;
import io.github.timpcunningham.anax.commands.world.CreateCommand;
import io.github.timpcunningham.anax.commands.world.MembersCommands;
import io.github.timpcunningham.anax.commands.world.delete.DeleteCommand;
import io.github.timpcunningham.anax.listeners.*;
import io.github.timpcunningham.anax.player.AnaxPlayer;
import io.github.timpcunningham.anax.player.AnaxPlayerManager;
import io.github.timpcunningham.anax.utils.chat.ComponentBuilder;
import io.github.timpcunningham.anax.utils.server.Debug;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import io.github.timpcunningham.anax.world.tables.Flags;
import io.github.timpcunningham.anax.world.tables.Role;
import io.github.timpcunningham.anax.world.tables.Spawn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Anax extends JavaPlugin {
    private static Anax self;
    private CommandsManager<CommandSender> commands;
    private Permission builderPermission;
    private UnloadListener unloadListener;

    @Override
    public void onLoad() {
        self = this;
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        loadBuilderPermissions();

        int unload_poll = getConfig().getInt("unload.poll-interval",10) * 20 * 60;
        int dropbox_poll = getConfig().getInt("dropbox.poll-interval", 2) * 20 * 60 * 60;
        unloadListener = UnloadListener.getInstance();
        unloadListener.runTaskTimer(this, unload_poll , unload_poll );
        Dropbox.getInstance().runTaskTimerAsynchronously(this, dropbox_poll, dropbox_poll);

        setupDatabase();
        setupCommands();
        setupListeners();

        AnaxPlayerManager.getInstance().createServerPlayer();
        WorldUtils.loadAllWorlds();
    }

    @Override
    public void onDisable() {
        AnaxWorldManagement.getInstance().unloadAll();
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

        cmdRegister.register(ChatCommands.class);
        cmdRegister.register(Confrim.class);
        cmdRegister.register(CreateCommand.class);
        cmdRegister.register(DeleteCommand.class);
        cmdRegister.register(DownloadCommand.class);
        cmdRegister.register(ImportCommand.class);
        cmdRegister.register(MapCommands.class);
        cmdRegister.register(MembersCommands.class);
        cmdRegister.register(ToggleCommand.class);
    }

    public void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new FlagListeners(), this);
        Bukkit.getPluginManager().registerEvents(new PermissionListener(), this);
        Bukkit.getPluginManager().registerEvents(new Playerlisteners(), this);
        Bukkit.getPluginManager().registerEvents(unloadListener, this);
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
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> dbClazz = new ArrayList<>();

        dbClazz.add(AnaxPlayer.class);
        dbClazz.add(AnaxWorld.class);
        dbClazz.add(Flags.class);
        dbClazz.add(Role.class);
        dbClazz.add(Spawn.class);

        return dbClazz;
    }

    public String getWorldBasePath() {
        return Bukkit.getWorldContainer().getPath() +  getConfig().getString("worlds.container");
    }

    public void loadBuilderPermissions() {
        ConfigurationSection section = getConfig().getConfigurationSection("builder.permissions");
        Map<String, Boolean> childern = new HashMap<>();

        for(String key : section.getKeys(true)) {
            if(section.getConfigurationSection(key) == null) {
                childern.put(key, section.getBoolean(key, false));
            }
        }

        builderPermission = new Permission("anax.world.builder", "World builder building permission", PermissionDefault.FALSE, childern);
    }

    public Permission getBuilderPermission() {
        return this.builderPermission;
    }
}