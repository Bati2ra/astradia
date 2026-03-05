package com.astradia.network;


import com.astradia.VentoClient;
import com.astradia.ClientCosmeticStore;
import com.astradia.network.payloads.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ClientNetworkManager {

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(CosmeticsDataPayload.ID, CosmeticsDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BodyProportionsDataPayload.ID, BodyProportionsDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerDataPayload.ID, PlayerDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerReadyPayload.ID, PlayerReadyPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerCosmeticEquipPayload.ID, PlayerCosmeticEquipPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(PlayerDataPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                String data = payload.data();
                JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
                VentoClient.getPlayerManager().receiveServerPlayerData(jsonObject);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(CosmeticsDataPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                String data = payload.data();
                JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
                ClientCosmeticStore.INSTANCE.receiveServerStore(jsonObject);

                // Indicar al servidor que el cliente esta listo para recibir mensajes
                ClientPlayNetworking.send(new PlayerReadyPayload());
            });
        });

        // Convertir en un handshake, si el cliente recibio cierta informacion, esta listo para recibir otra relacionada
        ClientPlayNetworking.registerGlobalReceiver(CosmeticsDataPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                String data = payload.data();
                JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
                ClientCosmeticStore.INSTANCE.receiveServerStore(jsonObject);

                // Indicar al servidor que el cliente esta listo para recibir mensajes
                ClientPlayNetworking.send(new PlayerReadyPayload());
            });
        });
    }
}
