package com.astradia.player;

import com.astradia.ClientCosmeticStore;
import com.astradia.api.CosmeticCategoryRegistry;
import com.astradia.api.player.PlayerCosmeticData;
import com.astradia.pojo.ClientCosmeticDefinition;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.astradia.VentoClient.LOGGER;

public class PlayerCosmetics extends PlayerFeature {

    protected final Map<Identifier, ClientCosmeticSlot> slots;

    public PlayerCosmetics(UUID playerId) {
        super("cosmetics", playerId);
        slots = CosmeticCategoryRegistry.buildSlotsFromCategories(ClientCosmeticSlot::new);
    }

    public void clearAll() {
        slots.values().forEach(ClientCosmeticSlot::clear);
    }

    public boolean equip(ClientCosmeticDefinition cosmetic, @Nullable Identifier preferredSlotId) {
        if (preferredSlotId != null) {
            ClientCosmeticSlot slot = slots.get(preferredSlotId);
            return slot != null && slot.equip(cosmetic);
        }

        // Auto: primer slot vacío de la categoría correcta
        for (ClientCosmeticSlot slot : slots.values()) {
            if (slot.getCategoryId().equals(cosmetic.getCategoryId()) && slot.isEmpty()) {
                return slot.equip(cosmetic);
            }
        }
        return false;
    }

    @Override
    public void fromJson(@NotNull JsonObject json) {
        clearAll();

        if(!json.has("equipped")) return;

        JsonObject equipped = json.get("equipped").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : equipped.entrySet()) {
            Identifier slotId = Identifier.tryParse(entry.getKey());
            if (slotId == null) continue;

            ClientCosmeticSlot slot = slots.get(slotId);
            if (slot == null) continue;

            try {
                PlayerCosmeticData data = new PlayerCosmeticData(
                        ClientCosmeticStore.INSTANCE,
                        entry.getValue().getAsJsonObject()
                );
                // Validar que el cosmético cargado pertenece a la categoría del slot
                if (!data.getCosmetic().getCategoryId().equals(slot.getCategoryId())) {
                    throw new Exception("Categoría incorrecta para slot '" + slotId + "'");
                }
                slot.setEquipped(data);
                slot.clearCache();
            } catch (Exception e) {
                // Log y dejar slot vacío, no cachear datos corruptos silenciosamente
                LOGGER.warn("Error cargando slot '{}': {}", slotId, e.getMessage());
                slot.clear();
                slot.cacheSlotData(entry.getValue().getAsJsonObject());
            }
        }
    }

    public Map<Identifier, ClientCosmeticSlot> getSlots() { return slots; }

    /** Obtiene todos los slots de una categoría específica */
    public List<ClientCosmeticSlot> getSlotsByCategory(Identifier categoryId) {
        return slots.values().stream()
                .filter(s -> s.getCategoryId().equals(categoryId))
                .toList();
    }
}
