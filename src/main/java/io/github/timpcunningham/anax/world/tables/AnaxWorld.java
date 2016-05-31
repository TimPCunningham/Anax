package io.github.timpcunningham.anax.world.tables;

import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.utils.world.WorldUtils;
import io.github.timpcunningham.anax.world.*;
import io.github.timpcunningham.anax.world.Access;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AnaxWorld - Holds information regarding a world and its settings
 */
@Entity
public class AnaxWorld {

    @Id
    private String fullName;
    private String shortName;

    //Flags
    @Transient
    Flags flags;

    //Spawn
    @Transient
    Spawn spawn;

    //RoleType Lists
    @Transient
    private Map<String, Map<UUID, Role>> roles;


    @Enumerated(value = EnumType.STRING)
    private Access access;
    @Enumerated(value = EnumType.STRING)
    private Phase phase;

    private boolean locked;
    private boolean loaded;

    @Transient
    World world;

    public AnaxWorld() {
        flags = new Flags();
        roles = new HashMap<>();
        loaded = false;
        spawn = new Spawn();
    }

    public void retrieveData() {
        try {
            this.spawn = (Spawn) AnaxDatabase.find(Spawn.class).where().eq("worldName", this.fullName).findUnique();
        } catch (Exception e) {
            this.spawn = new Spawn();
        }

        Flags dbFlags = (Flags) AnaxDatabase.find(Flags.class).where().eq("world", this.fullName).findUnique();
        if(dbFlags != null) {
            flags = dbFlags;
        }

        for(RoleType type : RoleType.values()) {
            List<Role> dbRoles = AnaxDatabase.find(Role.class).where().eq("world", fullName).eq("type", type).findList();
            roles.put(type.name(), new HashMap<>());

            for(Role role : dbRoles) {
                roles.get(type.name()).put(role.getPlayer(), role);
            }
        }
    }

    public void saveData() {
        AnaxDatabase.update(spawn);

        for(Map<UUID,Role> roleList : roles.values()) {
            for(Role role : roleList.values()) {
                AnaxDatabase.update(role);
            }
        }

        AnaxDatabase.update(flags);
    }

    /**
     * Sets defaults for an Anax world
     */
    public void setDefaults() {
        flags.setWorldName(this.fullName);

        //Spawn defaults
        spawn.setWorldName(this.fullName);
        spawn.setX(0.5);
        spawn.setY(0.5);
        spawn.setZ(0.5);
        spawn.setYaw(90);
        spawn.setPitch(0);

        //Roles defaults
        for(RoleType type : RoleType.values()) {
            roles.put(type.name(), new HashMap<>());
        }

        access = Access.PUBLIC;
        phase = Phase.DEVELOPMENT;
        locked = false;
        loaded = true;

        AnaxDatabase.save(spawn);
        AnaxDatabase.save(flags);
    }

    /**
     * Toggles a flag in this Anax world
     *
     * @param type The flag to be toggled
     * @return Returns the new value of the flag that was toggled
     */
    public boolean toggleFlag(FlagType type) {
        return flags.toggle(type);
    }

    /**
     * Gets the value of a specified flag
     *
     * @param type the flag to get the value of
     * @return the flags value
     */
    public boolean getFlagValue(FlagType type) {
        return flags.isEnabled(type);
    }


    public Map<String, Flags.FlagContianer> getFlags() {
        return flags.getFlags();
    }

    /**
     * Gets spawn location in a form that can be easily used by bukkit
     *
     * @return returns the spawn as a Location
     */
    public Location getSpawn() {
            return spawn.toLocation(this.getWorld());
    }

    /**
     * Sets the spawn from a Bukkit Location
     *
     * @param location The Location that should  be set as spawn
     */
    public void setSpawn(Location location) {
        spawn.setX(location.getX());
        spawn.setY(location.getY());
        spawn.setZ(location.getZ());
        spawn.setYaw(location.getYaw());
        spawn.setPitch(location.getPitch());
        AnaxDatabase.update(spawn);
    }

    /**
     * Toggles the locked status of this world
     *
     * @return Returns the new locked status of this world
     */
    @Transient
    public boolean toggleLock() {
        setLocked(!isLocked());
        return isLocked();
    }

    /**
     * Adds a player to a specific role in this world
     *
     * @param type The role the player should be added to
     * @param member The player that is to be added
     */
    @Transient
    public void addMemeber(RoleType type, UUID member) {
        Role role = new Role(this.fullName, type, member);
        roles.get(type.name()).put(member, role);
        AnaxDatabase.save(role);
    }

    /**
     * Removes a player from a specific role in this world
     *
     * @param type The role the player should be removed from
     * @param member The player that is to be removed
     */
    @Transient
    public void removeMember(RoleType type, UUID member) {
        Role role = roles.get(type.name()).get(member);

        if(role != null) {
            getMemeberList(type).remove(member);
            AnaxDatabase.delete(role);
        }
    }

    /**
     * Gets a list of members that belong to a specific role in this world
     *
     * @param type The role that we wish to get a list of players for
     * @return A list of UUID's that fit this role
     */
    public Set<UUID> getMemeberList(RoleType type) {
        return roles.get(type.name()).keySet();
    }

    public boolean isRole(RoleType type, UUID uuid) {
        return getMemeberList(type).contains(uuid);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public String shortInfo() {
        List<UUID> owners = getMemeberList(RoleType.OWNER).stream().collect(Collectors.toList());
        return  ChatColor.LIGHT_PURPLE + this.shortName + " by " + WorldUtils.uuidsToListing(owners);
    }

    /**
     * ===============================================================================================================
     * Database required methods
     * ===============================================================================================================
     */

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
