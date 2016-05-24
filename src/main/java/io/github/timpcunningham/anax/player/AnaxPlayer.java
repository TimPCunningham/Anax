package io.github.timpcunningham.anax.player;

import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class AnaxPlayer {
    @Id
    UUID uuid;
    String name;
    Date firstJoin;
    @Enumerated(value = EnumType.STRING)
    Channel channel;
    long blocksPlaced;
    long blocksBroken;
    int joins;

    @Transient
    Player player;

    public AnaxPlayer() {
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    //region DB Methods

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(Date firstJoin) {
        this.firstJoin = firstJoin;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public long getBlocksPlaced() {
        return blocksPlaced;
    }

    public void setBlocksPlaced(long blocksPlaced) {
        this.blocksPlaced = blocksPlaced;
    }

    public long getBlocksBroken() {
        return blocksBroken;
    }

    public void setBlocksBroken(long blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public int getJoins() {
        return joins;
    }

    public void setJoins(int joins) {
        this.joins = joins;
    }

    //endregion
}