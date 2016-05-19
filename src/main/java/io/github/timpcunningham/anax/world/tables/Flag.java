package io.github.timpcunningham.anax.world.tables;

import io.github.timpcunningham.anax.utils.Lang;
import io.github.timpcunningham.anax.world.FlagType;

import javax.persistence.*;

@Entity
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String world;
    @Enumerated(value = EnumType.STRING)
    FlagType type;
    @Enumerated(value = EnumType.STRING)
    Lang description;
    boolean enabled;

    public Flag() { }

    public Flag(String world, FlagType type, Boolean enabled, Lang description) {
        this.world = world;
        this.type = type;
        this.enabled = enabled;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public FlagType getType() {
        return type;
    }

    public void setType(FlagType type) {
        this.type = type;
    }

    public Lang getDescription() {
        return description;
    }

    public void setDescription(Lang description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean toggle() {
        this.enabled = !this.enabled;
        return this.enabled;
    }
}
