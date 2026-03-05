package com.astradia.network.c2s;

import com.astradia.VentoServer;
import com.astradia.api.CosmeticDefinition;
import com.astradia.enums.ResponseType;
import com.astradia.network.payloads.PlayerCosmeticEquipPayload;
import com.astradia.player.PlayerCosmetics;
import com.astradia.player.PlayerData;
import com.astradia.store.ServerCosmeticStore;
import com.astradia.utils.CosmeticResponse;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;

import static com.astradia.VentoServer.LOGGER;

public class PlayerCosmeticEquipRequest {
    PlayerCosmeticEquipPayload payload;
    ServerPlayNetworking.Context context;

    public PlayerCosmeticEquipRequest(PlayerCosmeticEquipPayload payload, ServerPlayNetworking.Context context) {
        this.payload = payload;
        this.context = context;
    }

    public void execute() {
        Identifier cosmeticId = payload.cosmeticId();
        Identifier slot = payload.slot();

        LOGGER.debug("Received cosmetic equip request from player '{}' -> cosmetic: {}, slot: {}",
                context.player().getName().getString(),
                cosmeticId,
                slot
        );

        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(context.player());
        PlayerCosmetics playerCosmetics = playerData.getCosmetics();

        CosmeticDefinition cosmeticDefinition = ServerCosmeticStore.INSTANCE.get(cosmeticId);
        if(cosmeticDefinition == null) {
            LOGGER.warn("Cosmetic '{}' not found in ServerCosmeticStore", cosmeticId);
            return;
        }

        CosmeticResponse response = playerCosmetics.equip(cosmeticDefinition, slot, null);

        if (response.getCode().equals(ResponseType.SUCCESS)) {
            VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(context.player());

            LOGGER.info("Cosmetic '{}' successfully equipped in slot '{}' for player '{}'",
                    cosmeticId,
                    slot,
                    context.player().getName().getString()
            );
        } else {
            LOGGER.warn("Failed to equip cosmetic '{}' in slot '{}' for player '{}'. Reason='{}'",
                    cosmeticId,
                    slot,
                    context.player().getName().getString(),
                    response.getCode()
            );
        }
    }
}
