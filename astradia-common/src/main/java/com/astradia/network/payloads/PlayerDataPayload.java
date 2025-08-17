package com.astradia.network.payloads;

import com.astradia.AstradiaConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record PlayerDataPayload(NbtCompound nbtCompound) implements CustomPayload {
    public static final CustomPayload.Id<PlayerDataPayload> ID = new CustomPayload.Id<>(AstradiaConstants.PLAYER_DATA_ID);
    public static final PacketCodec<RegistryByteBuf, PlayerDataPayload> CODEC = PacketCodec.tuple(PacketCodecs.NBT_COMPOUND, PlayerDataPayload::nbtCompound, PlayerDataPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
