package com.astradia.network;


import com.astradia.ClientCosmeticStore;
import com.astradia.ClientPlayerCosmeticManager;
import com.astradia.network.payloads.CosmeticsDataPayload;
import com.astradia.network.payloads.PlayerCosmeticsDataPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class ClientNetworkManager {

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(PlayerCosmeticsDataPayload.ID, PlayerCosmeticsDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CosmeticsDataPayload.ID, CosmeticsDataPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(PlayerCosmeticsDataPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                NbtCompound data = payload.nbtCompound();
                System.out.println("Received data:");
                ClientPlayerCosmeticManager.INSTANCE.receiveServerPlayerData(data);
                System.out.println("UUID: " + data.getUuid("uuid"));
                NbtCompound cosmeticsData = data.getCompound("cosmetics");
                for (String key : cosmeticsData.getKeys()) {
                    System.out.println(key + " " + cosmeticsData.get(key).toString());
                }
            });
        });

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
            });
        });
    }
}
