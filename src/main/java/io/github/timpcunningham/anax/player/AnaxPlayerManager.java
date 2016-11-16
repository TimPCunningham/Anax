package io.github.timpcunningham.anax.player;

import io.github.timpcunningham.anax.utils.chat.Chat;
import io.github.timpcunningham.anax.utils.server.AnaxDatabase;
import io.github.timpcunningham.anax.world.AnaxWorldManagement;
import io.github.timpcunningham.anax.world.tables.AnaxWorld;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnaxPlayerManager {
    private static AnaxPlayerManager self;
    private static final String serverUUID = "00000000-0000-0000-0000-000000000000";
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
        }

        anaxPlayer.setName(player.getName()); //update name when adding
        anaxPlayer.setPlayer(player);
        anaxPlayer.setJoins(anaxPlayer.getJoins() + 1);
        AnaxDatabase.save(anaxPlayer);

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

    public String getName(UUID uuid) {
        AnaxPlayer player = this.getAnaxPlayer(uuid);
        if(player == null) {
            return "Unknown";
        }
        return player.getName();
    }

    public AnaxPlayer getAnaxPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public AnaxPlayer findAnaxPlayer(String name) {
        AnaxPlayer result = (AnaxPlayer) AnaxDatabase.find(AnaxPlayer.class).where().eq("name", name).findUnique();

        if(result != null && players.containsKey(result.getUuid())) {
            return players.get(result.getUuid());
        }
        return result;
    }

    public AnaxPlayer getServerAsPlayer() {
        return players.get(UUID.fromString(serverUUID));
    }

    public void createServerPlayer() {
        AnaxPlayer serverPlayer = AnaxDatabase.getAnaxPlayer(UUID.fromString(serverUUID));

        if(serverPlayer == null) {
            serverPlayer = new AnaxPlayer();
            serverPlayer.setPlayer(null);
            serverPlayer.setUuid(UUID.fromString(serverUUID));
            serverPlayer.setName("Anax");
            serverPlayer.setFirstJoin(new Date());
            serverPlayer.setJoins(Integer.MAX_VALUE);
            AnaxDatabase.save(serverPlayer);
        }

        players.put(serverPlayer.getUuid(), serverPlayer);
    }

    public void sendToHub(AnaxWorld world) {
        AnaxWorld hub = AnaxWorldManagement.getInstance().getDefaultWorld();

        for(Player player : world.getWorld().getPlayers()) {
            try {
                player.teleport(hub.getSpawn());
            } catch (Exception e) {
                Chat.alertConsole("Couldn't teleport " + player.getName() + " to the hub!");
            }
        }
    }
}
