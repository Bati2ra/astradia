package com.astradia.player;

import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData implements IPlayerData {
    private final UUID playerId;
    private final PlayerCosmetics cosmetics;
    protected final Map<Class<? extends PlayerFeature>, PlayerFeature> features;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.features = new HashMap<>();
        cosmetics = new PlayerCosmetics(playerId);
        features.put(PlayerCosmetics.class, cosmetics);
    }

    public <T extends PlayerFeature> T getFeature(Class<T> featureClass) {
        return (T) features.get(featureClass);
    }

    @Override
    public UUID getUuid() {
        return playerId;
    }

    public PlayerCosmetics getCosmetics() {
        return cosmetics;
    }

    @Override
    public void fromNbt(NbtCompound tag) {
        for (PlayerFeature value : features.values()) {
            NbtCompound nbtTag = tag.getCompound(value.identifier);
            value.deserialize(nbtTag);
        }
    }
}
