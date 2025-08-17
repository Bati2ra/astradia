package com.astradia.network;


import com.astradia.AstradiaClient;
import com.astradia.ClientCosmeticStore;
import com.astradia.network.payloads.CosmeticsDataPayload;
import com.astradia.network.payloads.PlayerDataPayload;
import com.astradia.network.payloads.PlayerReadyPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class ClientNetworkManager {

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(CosmeticsDataPayload.ID, CosmeticsDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerDataPayload.ID, PlayerDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerReadyPayload.ID, PlayerReadyPayload.CODEC);


        ClientPlayNetworking.registerGlobalReceiver(PlayerDataPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                NbtCompound data = payload.nbtCompound();
                System.out.println("Received data:");
                System.out.println("Data: " + data.asString());
                AstradiaClient.getPlayerManager().receiveServerPlayerData(data);
                NbtCompound cosmeticsData = data.getCompound("cosmetics");
                for (String key : cosmeticsData.getKeys()) {
                    System.out.println(key + " " + cosmeticsData.get(key).toString());
                }
            });
        });

        // Convertir en un handshake, si el cliente recibio cierta informacion, esta listo para recibir otra relacionada
        ClientPlayNetworking.registerGlobalReceiver(CosmeticsDataPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                NbtCompound data = payload.nbtCompound();
                NbtList list = (NbtList) data.get("cosmetics");
                System.out.println("Received cosmetics data:");
                ClientCosmeticStore.INSTANCE.receiveServerStore(data);
                for (NbtElement key : list) {
                    if(key instanceof NbtCompound nbtCompound) {
                        System.out.println(nbtCompound);
                    }
                }
                // Indicar al servidor que el cliente esta listo para recibir mensajes
                ClientPlayNetworking.send(new PlayerReadyPayload());
            });
        });
    }
}
