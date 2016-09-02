package io.github.timpcunningham.anax.world.tables;

import io.github.timpcunningham.anax.utils.chat.Lang;
import io.github.timpcunningham.anax.world.types.FlagType;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Flags {
    @Id
    String worldName;

    boolean animals;
    boolean explosions;
    boolean fire;
    boolean monsters;
    boolean physics;
    boolean weather;
    boolean world;

    public Flags() {
        //defaults
        animals = false;
        explosions = false;
        fire = false;
        monsters = false;
        physics = true;
        weather = false;
        world = true;
    }

    public boolean toggle(FlagType type) {
        switch (type) {
            case ANIMALS:
                animals = !animals;
                return animals;
            case EXPLOSIONS:
                explosions = !explosions;
                return explosions;
            case FIRE:
                fire = !fire;
                return fire;
            case MONSTERS:
                monsters = !monsters;
                return monsters;
            case PHYSICS:
                physics = !physics;
                return physics;
            case WEATHER:
                weather = !weather;
                return weather;
            case WORLD:
                world = !world;
                return world;
            default:
                return false;
        }
    }

    public boolean isEnabled(FlagType type) {
        switch (type) {
            case ANIMALS:
                return animals;
            case EXPLOSIONS:
                return explosions;
            case FIRE:
                return fire;
            case MONSTERS:
                return monsters;
            case PHYSICS:
                return physics;
            case WEATHER:
                return weather;
            case WORLD:
                return world;
            default:
                return false;
        }
    }

    public Lang getDescription(FlagType type) {
        switch (type) {
            case ANIMALS:
                return Lang.FLAG_DESC_ANIMALS;
            case EXPLOSIONS:
                return Lang.FLAG_DESC_EXPLOSIONS;
            case FIRE:
                return Lang.FLAG_DESC_FIRE;
            case MONSTERS:
                return Lang.FLAG_DESC_MONSTERS;
            case PHYSICS:
                return Lang.FLAG_DESC_PHYSICS;
            case WEATHER:
                return Lang.FLAG_DESC_WEATHER;
            case WORLD:
                return Lang.FLAG_DESC_WORLD;
            default:
                return null;
        }
    }

    public Map<String, FlagContianer> getFlags() {
        Map<String, FlagContianer> flags = new HashMap<>();

        for(FlagType type : FlagType.values()) {
            flags.put(type.name(), new FlagContianer(isEnabled(type), getDescription(type)));
        }

        return  flags;
    }

    //region DB Methods
    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public boolean isAnimals() {
        return animals;
    }

    public void setAnimals(boolean animals) {
        this.animals = animals;
    }

    public boolean isExplosions() {
        return explosions;
    }

    public void setExplosions(boolean explosions) {
        this.explosions = explosions;
    }

    public boolean isMonsters() {
        return monsters;
    }

    public void setMonsters(boolean monsters) {
        this.monsters = monsters;
    }

    public boolean isPhysics() {
        return physics;
    }

    public void setPhysics(boolean physics) {
        this.physics = physics;
    }

    public boolean isWeather() {
        return weather;
    }

    public void setWeather(boolean weather) {
        this.weather = weather;
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }

    public boolean isWorld() {
        return world;
    }

    public void setWorld(boolean world) {
        this.world = world;
    }
    //endregion

    //region ContainerClass
    public class FlagContianer {
        public Lang description;
        public boolean value;

        public FlagContianer(boolean value, Lang description) {
            this.value = value;
            this.description = description;
        }
    }
    //endregion
}
