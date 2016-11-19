package io.github.timpcunningham.anax.commands.downlaod;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.exceptions.LocalizedCommandException;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.utils.Fuzzy;
import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.Callback;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.utils.server.Debug;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.types.RoleType;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Dropbox extends BukkitRunnable {
    private static Dropbox self;
    private DbxClientV2 client;
    private boolean canPrune = false;
    private WorldEditPlugin wePlugin;
    private boolean isSupported = true;

    private Dropbox() {
        try {
            DbxRequestConfig config = new DbxRequestConfig("Anax", Locale.getDefault().toString());
            client = new DbxClientV2(config, Anax.get().getConfig().getString("dropbox.token"));

            try { client.files().createFolder("/downloads"); }
            catch (CreateFolderErrorException e) { }

            try { client.files().createFolder("/import"); }
            catch (CreateFolderErrorException e) { }

            Chat.alertConsole("[Anax] Dropbox integrated!");
        } catch (Exception e) {
            e.printStackTrace();
            isSupported = false;
        }

        try {
            //wePlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            //canPrune = true;
        } catch (Exception e) {
            Debug.severe("Could not find WorldEdit! Pruning disabled!");
        }
    }

    public static Dropbox getInstance() {
        if(self == null) {
            self = new Dropbox();
        }
        return self;
    }

    public AnaxWorld importWorld(Player player, UUID owner, String worldName, String newName) throws LocalizedCommandException {
        AnaxWorld world = new AnaxWorld();
        String path = Anax.get().getWorldBasePath() + owner + "/" + newName;
        List<String> possibilities = new ArrayList<>();
        String bestMatch;

        if(AnaxDatabase.isWorld(newName)) {
            throw new LocalizedCommandException(player, Lang.COMMAND_IMPORT_DUPLICATE);
        }

        try {
            possibilities = client.files().listFolder("/import").getEntries()
                    .stream().map(Metadata::getName).collect(Collectors.toList());
        } catch (DbxException e) { }

        bestMatch = Fuzzy.findBestMatch(worldName, possibilities);
        if(bestMatch.equalsIgnoreCase("")) {
            throw new LocalizedCommandException(player, Lang.COMMAND_IMPORT_ERROR, worldName);
        }

        try {
            download("/import/" + bestMatch, path);
            client.files().delete("/import/" + bestMatch);
        } catch (LocalizedException e) {
            throw new LocalizedCommandException(player, e.getReason(), e.getArgs());
        } catch (DbxException dxe) {
            Chat.alertConsole("Could not delete folder " + bestMatch);
            dxe.printStackTrace();
        }

        world.setFullName(path);
        world.setShortName(newName);
        world.setDefaults();
        world.addMemeber(RoleType.OWNER, player.getUniqueId());

        return world;
    }

    private void download(String toDownload, String dest) throws LocalizedException {
        new File(dest).mkdirs();

        try {
            for(Metadata entry : client.files().listFolder(toDownload).getEntries()) {
                if(entry instanceof FolderMetadata && verifyFile(entry.getName())) {
                    download(toDownload + "/" + entry.getName(), dest + "/" + entry.getName());
                } else if(entry instanceof FileMetadata) {
                    DbxDownloader downloader = client.files().download(entry.getPathLower());
                    OutputStream outputStream = new FileOutputStream(dest + "/" + entry.getName());

                    downloader.download(outputStream);
                    outputStream.close();
                    downloader.close();
                }
            }
        } catch (DbxException | IOException e) {
            e.printStackTrace();
            throw new LocalizedException(Lang.COMMAND_IMPORT_ERROR, toDownload);
        }
    }

    private boolean verifyFile(String name) {
        return  name.endsWith(".dat") ||
                name.endsWith(".mca") ||
                name.endsWith(".png") ||
                name.endsWith(".xml");
    }

    public void upload(Player player, String path, String world, Callback callback) throws LocalizedException {
        try {
            InputStream in = new FileInputStream(path);
            FileMetadata metadata = client.files().uploadBuilder("/downloads/" + world + ".zip")
                    .withMode(WriteMode.OVERWRITE).uploadAndFinish(in);

            String url;

            try {
                url = client.sharing().createSharedLinkWithSettings(metadata.getPathDisplay()).getUrl();
            } catch (Exception e) {
                url = client.sharing().listSharedLinksBuilder().withPath(metadata.getPathDisplay()).withDirectOnly(true).start().getLinks().get(0).getUrl();
            }

            in.close();
            callback.execute(player, url, path);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
            throw new LocalizedException(Lang.COMMAND_DOWNLOAD_ERROR);
        }
    }

    public String zip(String path) throws LocalizedException {
        try {
            ZipFile file = new ZipFile(path + ".zip");
            ZipParameters parameters = new ZipParameters();

            file.addFolder(new File(path + "/region"), parameters);
            file.addFolder(new File(path + "/data"), parameters);
            file.addFile(new File(path + "/level.dat"), parameters);

            return file.getFile().getAbsolutePath();
        } catch (ZipException e) {
            e.printStackTrace();
            throw new LocalizedException(Lang.COMMAND_DOWNLOAD_ERROR);
        }
    }

    public void prune(String path) {
        //TODO - Add pruning
    }

    public boolean isSupported() {
        return this.isSupported;
    }

    @Override
    public void run() {
        if(!isSupported()) {
            this.cancel();
        }

        Chat.alertConsole("Checking dropbox for expired files...");

        try {
            ListRevisionsResult result = client.files().listRevisions("/downloads");
            Date expire = new Date(System.currentTimeMillis() - (3L * 24 * 3600 * 1000));

            for(FileMetadata meta : result.getEntries()) {
                if(meta.getClientModified().before(expire)) {
                    Chat.alertConsole("Dropbox file " + meta.getName() + " is expired. Deleting...");
                    client.files().permanentlyDelete(meta.getPathLower());
                }
            }
        } catch (DbxException e) {
            Chat.alertConsole(ChatColor.RED + "Could not delete old dropbox files!");
        }
    }
}
