package com.astradia.network.payloads;

import com.astradia.AstradiaConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record BodyProportionsDataPayload(String data) implements CustomPayload {
    public static final CustomPayload.Id<BodyProportionsDataPayload> ID = new CustomPayload.Id<>(AstradiaConstants.BODY_PROPORTIONS_DATA_ID);
    public static final PacketCodec<RegistryByteBuf, BodyProportionsDataPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, BodyProportionsDataPayload::data, BodyProportionsDataPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
