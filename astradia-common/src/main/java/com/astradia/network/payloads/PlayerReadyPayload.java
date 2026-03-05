package com.astradia.network.payloads;

import com.astradia.VentoConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PlayerReadyPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerReadyPayload> ID = new CustomPacketPayload.Type<>(VentoConstants.PLAYER_READY_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerReadyPayload> CODEC =
            StreamCodec.unit(new PlayerReadyPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
