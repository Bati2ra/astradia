package com.astradia.network;

import com.astradia.network.payloads.CosmeticsDataPayload;
import com.astradia.network.payloads.PlayerCosmeticsDataPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NetworkManager {

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(PlayerCosmeticsDataPayload.ID, PlayerCosmeticsDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CosmeticsDataPayload.ID, CosmeticsDataPayload.CODEC);
    }
    public static void sendToAllPlayers() {

    }

    public static void sendToPlayer() {

    }

    public static void receiveHandshake() {

    }
}
