package io.github.timpcunningham.anax.commands.downlaod;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.exceptions.LocalizedException;
import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.utils.command.Callback;
import io.github.timpcunningham.anax.utils.server.Debug;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class Dropbox {
    private static Dropbox self;
    private DbxClientV2 client;
    private boolean canPrune = false;
    private WorldEditPlugin wePlugin;

    private Dropbox() {
        DbxRequestConfig config = new DbxRequestConfig("Anax", Locale.getDefault().toString());
        client = new DbxClientV2(config, Anax.get().getConfig().getString("dropbox.token"));

        try {
            //wePlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            canPrune = true;
        } catch (Exception e) {
            Debug.server("Could not find WorldEdit! Pruning disabled!");
        }
    }

    public static Dropbox getInstance() {
        if(self == null) {
            self = new Dropbox();
        }
        return self;
    }

    public void upload(Player player, String path, String world, Callback callback) throws LocalizedException {
        try {
            InputStream in = new FileInputStream(path);
            FileMetadata metadata = client.files().uploadBuilder("/" + world + ".zip")
                    .withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
            String url = client.sharing().createSharedLinkWithSettings(metadata.getPathDisplay()).getUrl();

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

    }
}
