package com.astradia.network;

import com.astradia.network.c2s.PlayerCosmeticEquipRequest;
import com.astradia.network.payloads.CosmeticsDataPayload;
import com.astradia.network.payloads.PlayerCosmeticEquipPayload;
import com.astradia.network.payloads.PlayerDataPayload;
import com.astradia.network.payloads.PlayerReadyPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class NetworkManager {

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(CosmeticsDataPayload.ID, CosmeticsDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerDataPayload.ID, PlayerDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerReadyPayload.ID, PlayerReadyPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerCosmeticEquipPayload.ID, PlayerCosmeticEquipPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PlayerReadyPayload.ID, (payload, ctx) -> {
        });

        ServerPlayNetworking.registerGlobalReceiver(PlayerCosmeticEquipPayload.ID, ((playerCosmeticEquipPayload, context) -> new PlayerCosmeticEquipRequest(playerCosmeticEquipPayload, context).execute()));
    }
    public static void sendToPlayer(ServerPlayer to, CustomPacketPayload payload) {
        ServerPlayNetworking.send(to, payload);
    }

    public static void sendToSelf(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToTrackingPlayers(ServerPlayer player, boolean withSelf, CustomPacketPayload payload) {
        if(withSelf) ServerPlayNetworking.send(player, payload);
        for (ServerPlayer serverPlayerEntity : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(serverPlayerEntity, payload);
            System.out.print(serverPlayerEntity.getDisplayName().getString() + ", ");
        }
    }
}
