package io.github.timpcunningham.anax.listeners;

import io.github.timpcunningham.anax.utils.player.EventUtils;
import io.github.timpcunningham.anax.world.FlagType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class FlagListeners implements Listener {

    //region Physic Events
    @EventHandler
    public void blockPhysics(BlockPhysicsEvent event) {
        boolean result = EventUtils.event(FlagType.PHYSICS, event.getBlock().getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void blockForm(BlockFromToEvent event) {
        boolean result = EventUtils.event(FlagType.PHYSICS, event.getBlock().getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void blockChange(EntityChangeBlockEvent event) {
        boolean result = EventUtils.event(FlagType.PHYSICS, event.getBlock().getWorld());
        event.setCancelled(result);
    }
    //endregion

    //region World Events
    @EventHandler
    public void blockFade(BlockFadeEvent event) {
        boolean result = EventUtils.event(FlagType.WORLD, event.getBlock().getWorld());
        event.setCancelled(result);
    }


    @EventHandler
    public void blockGrow(BlockGrowEvent event) {
        boolean result = EventUtils.event(FlagType.WORLD, event.getBlock().getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void blockSpread(BlockSpreadEvent event) {
        boolean result = EventUtils.event(FlagType.WORLD, event.getBlock().getWorld());
        event.setCancelled(result);
    }


    @EventHandler
    public void blockMove(BlockFromToEvent event) {
        boolean result = EventUtils.event(FlagType.WORLD, event.getBlock().getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void blockLeaf(LeavesDecayEvent event) {
        boolean result = EventUtils.event(FlagType.WORLD, event.getBlock().getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void structureGrow(StructureGrowEvent event) {
        boolean result = EventUtils.event(FlagType.WORLD, event.getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void entityInteract(EntityInteractEvent event) {
        boolean result = EventUtils.event(FlagType.WORLD, event.getBlock().getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void entityFormBlock(EntityBlockFormEvent event) {
        boolean reuslt = EventUtils.event(FlagType.WORLD, event.getBlock().getWorld());
        event.setCancelled(reuslt);
    }
    //endregion

    //region Fire Events
    @EventHandler
    public void blockIgnite(BlockIgniteEvent event) {
        boolean result = EventUtils.event(FlagType.FIRE, event.getBlock().getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void blockBurn(BlockBurnEvent event) {
        boolean result = EventUtils.event(FlagType.FIRE, event.getBlock().getWorld());
        event.setCancelled(true);
    }
    //endregion

    //region Weather Event
    @EventHandler
    public void weatherChange(WeatherChangeEvent event) {
        boolean result = EventUtils.event(FlagType.WEATHER, event.getWorld());
        event.setCancelled(result && event.toWeatherState());
    }
    //endregion

    //region Explosion Events
    @EventHandler
    public void entityExplode(EntityExplodeEvent event) {
        boolean result = EventUtils.event(FlagType.EXPLOSIONS, event.getEntity().getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void blockExplode(BlockExplodeEvent event) {
        boolean result = EventUtils.event(FlagType.EXPLOSIONS, event.getBlock().getWorld());
        event.setCancelled(result);
    }

    @EventHandler
    public void hangingBreak(HangingBreakEvent event) {
        boolean result = EventUtils.event(FlagType.EXPLOSIONS, event.getEntity().getWorld());
        event.setCancelled(result && event.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION));
    }
    //endregion

    //region Monster Event
    @EventHandler
    public void hostileSpawn(CreatureSpawnEvent event) {
        boolean result = EventUtils.event(FlagType.MONSTERS, event.getEntity().getWorld());
        event.setCancelled(result && isHostile(event.getEntity()));
    }
    //endregion

    //region Animals Event
    @EventHandler
    public void passiveSpawn(CreatureSpawnEvent event) {
        boolean result = EventUtils.event(FlagType.ANIMALS, event.getEntity().getWorld());
        event.setCancelled(result && isPassive(event.getEntity()));
    }
    //endregion

    //region Util Methods
    private boolean isHostile(Entity entity) {
        return entity instanceof Monster ||
               entity instanceof Ghast   ||
               entity instanceof Slime   ||
               entity instanceof Shulker;
    }

    private boolean isPassive(Entity entity) {
        return entity instanceof Animals   ||
               entity instanceof IronGolem ||
               entity instanceof Snowman   ||
               entity instanceof Squid     ||
               entity instanceof Villager;
    }
    //endregion
}