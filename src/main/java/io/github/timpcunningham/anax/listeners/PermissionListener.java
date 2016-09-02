package io.github.timpcunningham.anax.listeners;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.events.PermissionChangedEvent;
import io.github.timpcunningham.anax.utils.server.Debug;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Map;

public class PermissionListener implements Listener{
    //private static final String BUILDER_PERM = "anax.world.builder";

    @EventHandler
    public void permChange(PermissionChangedEvent event) {

        //remove builder permission
        for(PermissionAttachmentInfo info : event.getPlayer().getEffectivePermissions()) {
            if(info.getPermission().equalsIgnoreCase("anax.world.builder")) {
                event.getPlayer().removeAttachment(info.getAttachment());
            }
        }

        //check to see if they need it
        if(event.getWorld().canBuild(event.getPlayer())) {
            PermissionAttachment attachment = event.getPlayer().addAttachment(Anax.get());
            attachment.setPermission(Anax.get().getBuilderPermission(), true);

            for(Map.Entry perm : Anax.get().getBuilderPermission().getChildren().entrySet()) {
                attachment.setPermission(String.valueOf(perm.getKey()), (boolean) perm.getValue());
            }
        }

        //recalculate
        event.getPlayer().recalculatePermissions();
    }
}
