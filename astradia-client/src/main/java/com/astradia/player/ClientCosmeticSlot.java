package com.astradia.player;

import com.astradia.AstradiaClient;
import com.astradia.ClientCosmeticStore;
import com.astradia.api.player.CosmeticSlot;
import com.astradia.api.player.PlayerCosmeticData;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public class ClientCosmeticSlot extends CosmeticSlot  {
    private JsonObject cachedSlotData;

    public ClientCosmeticSlot(Identifier name, String category) {
        super(name, category);
    }

    public void cacheSlotData(JsonObject json) {
        this.cachedSlotData = json;
    }

    public void clearCache() {
        this.cachedSlotData = null;
    }

    @Override
    public PlayerCosmeticData getCosmeticData() {
        var store = ClientCosmeticStore.INSTANCE;

        if(store.isReady() && cachedSlotData != null) {
            System.out.println(cachedSlotData.toString());
            try {
                setCosmeticData(new PlayerCosmeticData(store, cachedSlotData));
            } catch (Exception e) {
                AstradiaClient.LOGGER.warn("Error applying cached cosmetic data", e);
            }
            cachedSlotData = null;
        }
        return super.getCosmeticData();
    }
}
