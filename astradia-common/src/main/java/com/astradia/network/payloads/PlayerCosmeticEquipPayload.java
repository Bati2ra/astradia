package com.astradia.network.payloads;

import com.astradia.VentoConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public record PlayerCosmeticEquipPayload(Identifier cosmeticId, @Nullable Identifier slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerCosmeticEquipPayload> ID = new CustomPacketPayload.Type<>(VentoConstants.PLAYER_COSMETIC_EQUIP_PAYLOAD);

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerCosmeticEquipPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public PlayerCosmeticEquipPayload decode(RegistryFriendlyByteBuf buf) {
                    Identifier cosmeticId = buf.readIdentifier();
                    boolean hasSlot = buf.readBoolean();
                    if(hasSlot) {
                        Identifier slot = buf.readIdentifier();
                        return new PlayerCosmeticEquipPayload(cosmeticId, slot);
                    }
                    return new PlayerCosmeticEquipPayload(cosmeticId, null);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, PlayerCosmeticEquipPayload value) {
                    buf.writeIdentifier(value.cosmeticId);
                    buf.writeBoolean(value.slot != null);
                    if(value.slot != null) {
                        buf.writeIdentifier(value.slot);
                    }
                }
            };
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
