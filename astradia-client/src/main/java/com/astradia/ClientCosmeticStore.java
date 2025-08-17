package com.astradia;

import com.astradia.pojo.ClientCosmetic;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class ClientCosmeticStore extends CosmeticStore<ClientCosmetic> {
    public static final ClientCosmeticStore INSTANCE = new ClientCosmeticStore();
    private boolean isReady;

    public void initialize() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(this::onLeave));
    }
    public void receiveServerStore(NbtCompound tag) {
        cosmetics.clear();
        NbtList list = (NbtList) tag.get("cosmetics");
        assert list != null;
        for (NbtElement nbtElement : list) {
            NbtCompound element = (NbtCompound) nbtElement;
            ClientCosmetic cosmetic = new ClientCosmetic(element);
            cosmetics.put(cosmetic.getId(), cosmetic);
        }
        isReady = true;
    }

    public boolean isReady() {
        return isReady;
    }

    public void onLeave() {
        cosmetics.clear();
        isReady = false;
    }
}
