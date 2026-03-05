package com.astradia.network.payloads;

import com.astradia.VentoConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record BodyProportionsDataPayload(String data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BodyProportionsDataPayload> ID = new CustomPacketPayload.Type<>(VentoConstants.BODY_PROPORTIONS_DATA_ID);
    public static final StreamCodec<FriendlyByteBuf, BodyProportionsDataPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, BodyProportionsDataPayload::data, BodyProportionsDataPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
