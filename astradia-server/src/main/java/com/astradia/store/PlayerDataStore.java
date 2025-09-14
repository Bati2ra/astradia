package com.astradia.store;

import com.astradia.player.PlayerData;
import com.astradia.store.impl.PlayerLocalCosmeticStore;
import com.astradia.store.impl.PlayerLocalProportionsStore;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class PlayerDataStore {
    private final Store<JsonObject, UUID> cosmeticStore;
    private final Store<JsonObject, UUID> proportionsStore;

    public PlayerDataStore(MinecraftServer server) {
        this.cosmeticStore = new PlayerLocalCosmeticStore(server);
        this.proportionsStore = new PlayerLocalProportionsStore(server);
    }

    public void save(UUID playerId, PlayerData data) {
        cosmeticStore.save(playerId, data.getCosmetics().toJson());
        proportionsStore.save(playerId, data.getProportions().toJson());
    }

    public PlayerData load(UUID playerId) {
        PlayerData playerData = new PlayerData(playerId);
        var cosmetics = cosmeticStore.findById(playerId);
        var proportions = proportionsStore.findById(playerId);
        cosmetics.ifPresent(json -> playerData.getCosmetics().fromJson(json));
        proportions.ifPresent(json -> playerData.getProportions().fromJson(json));
        return playerData;
    }
}
