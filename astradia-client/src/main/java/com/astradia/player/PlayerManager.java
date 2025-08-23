package com.astradia.player;

import com.astradia.AstradiaClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    protected final HashMap<UUID, PlayerData> players;

    private static final PlayerData defaulted = new PlayerData(null);

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
        /*
        UUID uuid = tag.getUuid("uuid");
        NbtCompound featuresTag = tag.getCompound("features");
        PlayerData playerData = new PlayerData(uuid);
        playerData.fromNbt(featuresTag);
        players.put(uuid, playerData);*/
        System.out.println("PlayerData: ");
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : json.entrySet()) {
            System.out.println(stringJsonElementEntry.getKey() + " / " + stringJsonElementEntry.getValue().toString());
        }
        System.out.println("cipote");
    }

    public PlayerData getFromUuid(UUID uuid) {
        return players.getOrDefault(uuid, defaulted);
    }

    public void onDisconnect() {
        players.clear();
        AstradiaClient.LOGGER.info("[Cosmetics] Limpiando caché de jugadores");
    }
}
