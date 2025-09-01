package com.astradia.network;

import com.astradia.network.payloads.CosmeticsDataPayload;
import com.astradia.network.payloads.PlayerDataPayload;
import com.astradia.network.payloads.PlayerReadyPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public class NetworkManager {

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(CosmeticsDataPayload.ID, CosmeticsDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerDataPayload.ID, PlayerDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerReadyPayload.ID, PlayerReadyPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PlayerReadyPayload.ID, (payload, ctx) -> {
        });
    }
    public static void sendToPlayer(ServerPlayerEntity to, CustomPayload payload) {
        ServerPlayNetworking.send(to, payload);
    }
}
