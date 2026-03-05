package com.astradia.player;

import com.astradia.VentoClient;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    protected final HashMap<UUID, PlayerData> players;

    private final HashMap<UUID, PlayerData> fakePlayers;

    private static final PlayerData defaulted = new PlayerData(UUID.randomUUID()); // eto ta mal

    public PlayerManager() {
        players = new HashMap<>();
        fakePlayers = new HashMap<>();
    }

    public void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            var player = client.player;
            if(player == null) return;

            if(player.tickCount % 1200 == 0) {
                //players.entrySet().removeIf(entry -> entry.getValue().getPlayer() == null);
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(this::onDisconnect));
    }

    private void onPlayerDisconnect(Player player) {
    }

    private void onPlayerConnect(Player player) {
    }

    public PlayerData getFromPlayer(Player player) {
        return getFromUuid(player.getUUID());
    }

    public void receiveServerPlayerData(JsonObject json) {
        PlayerData playerData = new PlayerData(json);
        System.out.println(json.toString());
        players.put(playerData.getUuid(), playerData);
    }

    public PlayerData getFromUuid(UUID uuid) {
        return players.getOrDefault(uuid, defaulted);
    }

    public PlayerData getFromFakeUuid(UUID uuid) {
        if(!fakePlayers.containsKey(uuid)) {
            fakePlayers.put(uuid, new PlayerData(uuid));
        }
        return fakePlayers.get(uuid);
    }

    public void clearFakePlayers() {
        fakePlayers.clear();
    }

    public void onDisconnect() {
        players.clear();
        VentoClient.LOGGER.info("[Cosmetics] Limpiando caché de jugadores");
    }
}
