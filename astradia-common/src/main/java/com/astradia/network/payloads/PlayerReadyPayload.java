package com.astradia.network.payloads;

import com.astradia.AstradiaConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PlayerReadyPayload() implements CustomPayload {
    public static final CustomPayload.Id<PlayerReadyPayload> ID = new CustomPayload.Id<>(AstradiaConstants.PLAYER_READY_ID);
    public static final PacketCodec<RegistryByteBuf, PlayerReadyPayload> CODEC =
            PacketCodec.unit(new PlayerReadyPayload());
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
