package com.astradia;

import com.astradia.api.CosmeticInfo;
import com.astradia.pojo.ClientCosmetic;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.Map;

public class ClientCosmeticStore extends CosmeticStore<CosmeticInfo> {
    public static final ClientCosmeticStore INSTANCE = new ClientCosmeticStore();
    private boolean isReady;

    public void initialize() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(this::onLeave));
    }
    public void receiveServerStore(JsonObject json) {
        cosmetics.clear();
        System.out.println("Client Cosmetics: ");
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : json.entrySet()) {
            System.out.println(stringJsonElementEntry.getKey() + " / " + stringJsonElementEntry.getValue().toString());
        }
        isReady = false;
    }

    public boolean isReady() {
        return isReady;
    }

    public void onLeave() {
        cosmetics.clear();
        isReady = false;
    }
}
