package com.astradia.network.payloads;

import com.astradia.VentoConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CosmeticsDataPayload(String data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CosmeticsDataPayload> ID = new CustomPacketPayload.Type<>(VentoConstants.COSMETICS_DATA_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CosmeticsDataPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, CosmeticsDataPayload::data, CosmeticsDataPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

