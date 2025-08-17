package com.astradia.network;

import com.astradia.AstradiaConstants;
import com.astradia.AstradiaServer;
import com.astradia.network.payloads.CosmeticsDataPayload;
import com.astradia.network.payloads.PlayerDataPayload;
import com.astradia.network.payloads.PlayerReadyPayload;
import com.astradia.player.PlayerData;
import com.astradia.player.PlayerManager;
import com.astradia.player.PlayerState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
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

    public static void sendToSelf(ServerPlayerEntity player, CustomPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToTrackingPlayers(ServerPlayerEntity player, boolean withSelf, CustomPayload payload) {
        if(withSelf) ServerPlayNetworking.send(player, payload);
        for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(serverPlayerEntity, payload);
            System.out.print(serverPlayerEntity.getDisplayName().getString() + ", ");
        }
    }
}
