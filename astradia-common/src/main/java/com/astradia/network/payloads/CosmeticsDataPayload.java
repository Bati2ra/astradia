package com.astradia.network.payloads;

import com.astradia.AstradiaConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CosmeticsDataPayload(String data) implements CustomPayload {
    public static final CustomPayload.Id<CosmeticsDataPayload> ID = new CustomPayload.Id<>(AstradiaConstants.COSMETICS_DATA_ID);
    public static final PacketCodec<RegistryByteBuf, CosmeticsDataPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, CosmeticsDataPayload::data, CosmeticsDataPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}

