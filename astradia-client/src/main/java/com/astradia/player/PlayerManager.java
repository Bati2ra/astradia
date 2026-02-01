package com.astradia.player;

import com.astradia.AstradiaClient;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    protected final HashMap<UUID, PlayerData> players;

    private static final PlayerData defaulted = new PlayerData(UUID.randomUUID()); // eto ta mal

    public PlayerManager() {
        players = new HashMap<>();
    }

    public void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            var player = client.player;
            if(player == null) return;

            if(player.age % 1200 == 0) {
                //players.entrySet().removeIf(entry -> entry.getValue().getPlayer() == null);
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(this::onDisconnect));
    }

    private void onPlayerDisconnect(PlayerEntity player) {
    }

    private void onPlayerConnect(PlayerEntity player) {
    }

    public PlayerData getFromPlayer(PlayerEntity player) {
        return getFromUuid(player.getUuid());
    }

    public void receiveServerPlayerData(JsonObject json) {
        PlayerData playerData = new PlayerData(json);
        System.out.println(json.toString());
        players.put(playerData.getUuid(), playerData);
    }

    public PlayerData getFromUuid(UUID uuid) {
        return players.getOrDefault(uuid, defaulted);
    }

    public void onDisconnect() {
        players.clear();
        AstradiaClient.LOGGER.info("[Cosmetics] Limpiando caché de jugadores");
    }
}
