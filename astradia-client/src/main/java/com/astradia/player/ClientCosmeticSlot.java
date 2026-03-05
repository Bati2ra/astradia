package com.astradia.player;

import com.astradia.VentoClient;
import com.astradia.ClientCosmeticStore;
import com.astradia.api.player.CosmeticSlot;
import com.astradia.api.player.PlayerCosmeticData;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

public class ClientCosmeticSlot extends CosmeticSlot  {
    private JsonObject cachedSlotData;

    public ClientCosmeticSlot(Identifier id, Identifier categoryId) {
        super(id, categoryId);
    }

    public void cacheSlotData(JsonObject json) {
        this.cachedSlotData = json;
    }

    public void clearCache() {
        this.cachedSlotData = null;
    }

    /* TODO: Arreglar esto, es un parche */
    @Override
    public PlayerCosmeticData getEquipped() {
        ClientCosmeticStore store = ClientCosmeticStore.INSTANCE;
        if(store.isReady() && cachedSlotData != null) {
            try {
                System.out.println(cachedSlotData.toString());
                setEquipped(new PlayerCosmeticData(store, cachedSlotData));
            } catch (Exception e) {
                VentoClient.LOGGER.warn("Error applying cached cosmetic data", e);
            }
        }
        cachedSlotData = null;
        return super.getEquipped();
    }
}
