package com.astradia.player;

import com.astradia.ClientCosmeticStore;
import com.astradia.api.player.CosmeticSlot;
import com.astradia.api.player.PlayerCosmeticData;
import com.astradia.utils.SlotUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCosmetics extends PlayerFeature {

    protected final Map<Identifier, ClientCosmeticSlot> equippedInventory;

    public PlayerCosmetics(UUID playerId) {
        super("cosmetics", playerId);
        equippedInventory = new HashMap<>();
        var slots = SlotUtils.getPlayerEquipmentSlots();
        for (Map.Entry<Identifier, CosmeticSlot> entry : slots.entrySet()) {
            equippedInventory.put(entry.getKey(), new ClientCosmeticSlot(entry.getValue().getName(), entry.getValue().getCategory()));
        }
    }

    public void clearSlots() {
        for (CosmeticSlot slot : equippedInventory.values()) {
            slot.clear();
        }
    }

    @Override
    public void fromJson(@NotNull JsonObject json) {
        clearSlots();
        JsonObject jsonEquipped = json.has("equipped") ? json.get("equipped").getAsJsonObject() : null;
        if (jsonEquipped != null) {
            for (Map.Entry<String, JsonElement> entry : jsonEquipped.entrySet()) {
                Identifier id;
                try {
                    id = Identifier.of(entry.getKey());
                } catch (Exception e) {
                    // Clave inválida → ignorar
                    continue;
                }
                var slot = equippedInventory.get(id);
                if(slot == null) {
                    // Slot no existe → ignorar
                    continue;
                }
                try {
                    slot.setCosmeticData(new PlayerCosmeticData(ClientCosmeticStore.INSTANCE, entry.getValue().getAsJsonObject()));
                    slot.clearCache();
                } catch (Exception e) {
                    e.printStackTrace();
                    slot.clear();
                    slot.cacheSlotData(entry.getValue().getAsJsonObject());
                }
            }
        }
    }

    public Map<Identifier, ClientCosmeticSlot> getEquippedInventory() {
        return equippedInventory;
    }
}
