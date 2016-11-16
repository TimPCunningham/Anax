package io.github.timpcunningham.anax.listeners;

import io.github.timpcunningham.anax.utils.player.EventUtils;
import io.github.timpcunningham.anax.world.types.FlagType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;

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

    //region Animals Event
    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        boolean passiveResult = EventUtils.event(FlagType.ANIMALS, event.getEntity().getWorld());
        boolean hostileResult = EventUtils.event(FlagType.MONSTERS, event.getEntity().getWorld());

        event.setCancelled((passiveResult && isPassive(event.getEntity())) || (hostileResult && isHostile(event.getEntity())));
    }
    //endregion

    //region locked world events
    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getPlayer().getWorld()));
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getPlayer().getWorld()));
    }

    @EventHandler
    public void bucketEmpty(PlayerBucketEmptyEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getPlayer().getWorld()));
    }

    @EventHandler
    public void bucketFill(PlayerBucketFillEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getPlayer().getWorld()));
    }

    @EventHandler
    public void itemDrop(PlayerDropItemEvent event) {
        if(EventUtils.lockEvent(event.getPlayer().getWorld())) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void itemPickup(PlayerPickupItemEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getPlayer().getWorld()));
    }

    @EventHandler
    public void arrowPickup(PlayerPickupArrowEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getPlayer().getWorld()));
    }

    @EventHandler
    public void vehicleEnter(VehicleEnterEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getEntered().getWorld()));
    }

    @EventHandler
    public void vehicleCreate(VehicleCreateEvent event) {
        if(EventUtils.lockEvent(event.getVehicle().getWorld())) {
            event.getVehicle().remove();
        }
    }

    @EventHandler
    public void vehicleDamage(VehicleDamageEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getVehicle().getWorld()));
    }

    @EventHandler
    public void vehicleDestroy(VehicleDestroyEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getVehicle().getWorld()));
    }

    @EventHandler
    public void shootProjectile(ProjectileLaunchEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getEntity().getWorld()));
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        InventoryType type = event.getInventory().getType();
        boolean allowed = type.equals(InventoryType.CREATIVE) || type.equals(InventoryType.PLAYER);

        event.setCancelled(!allowed && EventUtils.lockEvent(event.getWhoClicked().getWorld()));
    }

    @EventHandler
    public void itemSpawn(ItemSpawnEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getEntity().getWorld()));
    }

    @EventHandler
    public void entityTarget(EntityTargetEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getEntity().getWorld()));
    }

    @EventHandler
    public void entityTame(EntityTameEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getEntity().getWorld()));
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        event.setCancelled(EventUtils.lockEvent(event.getEntity().getWorld()));
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
               entity instanceof Bat       ||
               entity instanceof Villager;
    }
    //endregion
}