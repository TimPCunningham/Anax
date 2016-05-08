package io.github.timpcunningham.anax.world;

import org.bukkit.Location;
import org.bukkit.World;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.*;

/**
 * AnaxWorld - Holds information regarding a world and its settings
 */
@Entity
public class AnaxWorld {

    @Id
    private String worldName;

    //Flags
    Map<String, Flag> flags;

    //Spawn
    Map<String, Double> location;
    Map<String, Float> direction;

    //Role Lists
    Map<Role, List<UUID>> roles;

    private Access access;

    private boolean locked;

    @Transient
    World world;

    public void AnaxWorld() {
        flags = new HashMap<>();
        location = new HashMap<>();
        direction = new HashMap<>();
        roles = new HashMap<>();
    }

    /**
     * Sets defaults for an Anax world
     */
    public void setDefaults() {
        //Flag defaults
        flags.put("animals", Flag.ANIMALS);
        flags.put("explosions", Flag.EXPLOSIONS);
        flags.put("monsters", Flag.MONSTERS);
        flags.put("physics", Flag.PHYSICS);
        flags.put("weather", Flag.WEATHER);

        //Spawn defaults
        location.put("x", 0.5);
        location.put("y", 1.0);
        location.put("z", 0.5);
        direction.put("yaw", 0f);
        direction.put("pitch", 0f);

        //Roles defaults
        roles.put(Role.OWNER, new ArrayList<UUID>());
        roles.put(Role.BUILDER, new ArrayList<UUID>());
        roles.put(Role.VISITOR, new ArrayList<UUID>());

        access = Access.PUBLIC;
        locked = false;
    }

    /**
     * Toggles a flag in this Anax world
     *
     * @param flag The flag to be toggled
     * @return Returns the new value of the flag that was toggled
     */
    public boolean toggleFlag(Flag flag) {
        return flags.get(flag.name().toLowerCase()).toggle();
    }

    /**
     * Gets spawn location in a form that can be easily used by bukkit
     *
     * @return returns the spawn as a Location
     */
    public Location getSpawn() {
        return new Location(
                this.world,
                location.get("x"),
                location.get("y"),
                location.get("z"),
                direction.get("yaw"),
                direction.get("pitch"));
    }

    /**
     * Sets the spawn from a Bukkit Location
     * @param spawn The Location that should  be set as spawn
     */
    public void setSpawn(Location spawn) {
        location.put("x",spawn.getX());
        location.put("y", spawn.getY());
        location.put("z", spawn.getZ());

        direction.put("yaw", spawn.getYaw());
        direction.put("pitch", spawn.getPitch());
    }

    /**
     * Toggles the locked status of this world
     * @return Returns the new locked status of this world
     */
    public boolean toggleLock() {
        setLocked(!isLocked());
        return isLocked();
    }

    /**
     * Adds a player to a specific role in this world
     * @param role The role the player should be added to
     * @param member The player that is to be added
     */
    public void addMemeber(Role role, UUID member) {
        roles.get(role.name().toLowerCase()).add(member);
    }

    /**
     * Removes a player from a specific role in this world
     * @param role The role the player should be removed from
     * @param member The player that is to be removed
     */
    public void removeMember(Role role, UUID member) {
        roles.get(role.name().toLowerCase()).remove(member);
    }

    /**
     * Gets a list of members that belong to a specific role in this world
     * @param role The role that we wish to get a list of players for
     * @return A list of UUID's that fit this role
     */
    public List<UUID> getMemeberList(Role role) {
        return roles.get(role.name().toLowerCase());
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

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public Map<String, Flag> getFlags() {
        return flags;
    }

    public void setFlags(Map<String, Flag> flags) {
        this.flags = flags;
    }

    public Map<String, Double> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Double> location) {
        this.location = location;
    }

    public Map<String, Float> getDirection() {
        return direction;
    }

    public void setDirection(Map<String, Float> direction) {
        this.direction = direction;
    }

    public Map<Role, List<UUID>> getRoles() {
        return roles;
    }

    public void setRoles(Map<Role, List<UUID>> roles) {
        this.roles = roles;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }


}
