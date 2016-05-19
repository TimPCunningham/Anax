package io.github.timpcunningham.anax.world.tables;

import io.github.timpcunningham.anax.world.RoleType;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String world;
    @Enumerated(value = EnumType.STRING)
    RoleType type;
    UUID player;

    public Role() {}

    public Role(String world, RoleType type, UUID player) {
        this.world = world;
        this.type = type;
        this.player = player;
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

    public RoleType getType() {
        return type;
    }

    public void setType(RoleType type) {
        this.type = type;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }
}
