package com.astradia.network.payloads;

import com.astradia.VentoConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PlayerDataPayload(String data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerDataPayload> ID = new CustomPacketPayload.Type<>(VentoConstants.PLAYER_DATA_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDataPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, PlayerDataPayload::data, PlayerDataPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
