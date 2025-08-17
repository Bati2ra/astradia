package com.astradia.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public abstract class PlayerFeature {
    protected final String identifier;
    protected final UUID uuid;
    protected boolean isDirty;

    public PlayerFeature(String identifier, UUID uuid) {
        this.identifier = identifier;
        this.uuid = uuid;
        this.isDirty = false;
    }
    public abstract NbtCompound serialize();
    public abstract void deserialize(NbtCompound tag);
    public abstract void sync(PlayerEntity player);

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
}
