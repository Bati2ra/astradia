package com.astradia.player;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public interface IPlayerData {
    UUID getUuid();
    NbtCompound toNbt();
    void fromNbt(NbtCompound tag);
}
