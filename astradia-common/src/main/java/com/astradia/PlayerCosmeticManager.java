package com.astradia;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public abstract class PlayerCosmeticManager<T extends PlayerCosmetics> {
    protected final HashMap<UUID, T> cosmetics = new HashMap<>();

    public void initialize() {}

    public T getFrom(ServerPlayerEntity player) {
        return getFrom(player.getUuid());
    }

    public T getFrom(UUID uuid) {
        return cosmetics.get(uuid);
    }

    public Collection<T> getAll() {
        return cosmetics.values();
    }

    public void remove(UUID uuid) {
        cosmetics.remove(uuid);
    }
}
