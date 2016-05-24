package io.github.timpcunningham.anax.player;

import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnaxPlayerManager {
    private static AnaxPlayerManager self;
    Map<UUID, AnaxPlayer> players;

    private AnaxPlayerManager() {
        players = new HashMap<>();
    }

    public static AnaxPlayerManager getInstance() {
        if(self == null) {
            self = new AnaxPlayerManager();
        }
        return self;
    }

    public void addPlayer(Player player) {
        AnaxPlayer anaxPlayer = (AnaxPlayer)AnaxDatabase.find(AnaxPlayer.class).where().eq("uuid", player.getUniqueId()).findUnique();
        if(anaxPlayer == null) {
            anaxPlayer = new AnaxPlayer();
            anaxPlayer.setUuid(player.getUniqueId());
            anaxPlayer.setFirstJoin(new Date());
            anaxPlayer.setChannel(Channel.GLOBAL);
            AnaxDatabase.save(anaxPlayer);
        }

        anaxPlayer.setName(player.getName()); //update name when adding
        anaxPlayer.setPlayer(player);
        anaxPlayer.setJoins(anaxPlayer.getJoins() + 1);

        players.put(player.getUniqueId(), anaxPlayer);
    }

    public void removePlayer(UUID uuid) {
        AnaxDatabase.update(players.get(uuid));
        players.remove(uuid);
    }

    public void saveAll() {
        for(AnaxPlayer player : players.values()) {
            AnaxDatabase.update(player);
        }
    }

    public AnaxPlayer getAnaxPlayer(UUID uuid) {
        return players.get(uuid);
    }
}
