package com.astradia.store;

import com.astradia.player.PlayerData;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class PlayerDataStore {
    private final Store<JsonObject, UUID> cosmeticStore;

    public PlayerDataStore(MinecraftServer server) {
        this.cosmeticStore = new PlayerLocalCosmeticStore(server);
    }

    public void saveAll(UUID playerId, PlayerData data) {
        cosmeticStore.save(playerId, data.getCosmetics().toJson());
    }

    public PlayerData load(UUID playerId) {
        PlayerData playerData = new PlayerData(playerId);
        var cosmetics = cosmeticStore.findById(playerId);
        cosmetics.ifPresent(persistable -> playerData.getCosmetics().fromJson(persistable));
        return playerData;
    }
}
