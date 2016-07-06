package io.github.timpcunningham.anax.listeners;

import io.github.timpcunningham.anax.Anax;
import io.github.timpcunningham.anax.events.PermissionChangedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

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
        }

        //recalculate
        event.getPlayer().recalculatePermissions();
    }
}
