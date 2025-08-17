package com.astradia.player;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public abstract class PlayerFeature {
    protected final String identifier;
    protected final UUID uuid;

    public PlayerFeature(String identifier, UUID uuid) {
        this.identifier = identifier;
        this.uuid = uuid;
    }

    public abstract void deserialize(NbtCompound tag);

    public void onDisconnect() {};
}
