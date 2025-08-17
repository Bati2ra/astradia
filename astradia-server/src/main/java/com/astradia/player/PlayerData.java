package com.astradia.player;

import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData implements IPlayerData {
    private final UUID playerId;
    private PlayerState status;
    private boolean loading;
    private final PlayerCosmetics cosmetics;
    protected final Map<Class<? extends PlayerFeature>, PlayerFeature> features;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.features = new HashMap<>();
        this.status = PlayerState.CONNECTING;
        this.loading = true;
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

    public PlayerState getStatus() {
        return status;
    }

    public PlayerCosmetics getCosmetics() {
        return cosmetics;
    }

    public void setStatus(PlayerState status) {
        this.status = status;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        NbtCompound featureTag = new NbtCompound();
        tag.putUuid("uuid", getUuid());
        for (PlayerFeature value : features.values()) {
            featureTag.put(value.identifier, value.serialize());
        }
        tag.put("features", featureTag);
        return tag;
    }

    @Override
    public void fromNbt(NbtCompound tag) {
        // TODO
        for (PlayerFeature value : features.values()) {
            NbtCompound nbtTag = tag.getCompound(value.identifier);
            value.deserialize(nbtTag);
        }
    }
}
