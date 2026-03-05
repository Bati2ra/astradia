package com.astradia.player;

import com.astradia.api.CosmeticCategoryRegistry;
import com.astradia.api.CosmeticDefinition;
import com.astradia.store.ServerCosmeticStore;
import com.astradia.api.player.CosmeticSlot;
import com.astradia.api.player.PlayerCosmeticData;
import com.astradia.enums.ResponseType;
import com.astradia.utils.CosmeticResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.astradia.VentoServer.LOGGER;

public class PlayerCosmetics extends PlayerFeature {
    protected final HashSet<Identifier> unlockedCosmetics = new HashSet<>();

    private final Map<Identifier, CosmeticSlot> slots;

    public PlayerCosmetics(UUID playerId) {
        super("cosmetics", playerId);
        slots = CosmeticCategoryRegistry.buildSlotsFromCategories();
    }

    public CosmeticResponse equip(CosmeticDefinition cosmetic, @Nullable Identifier preferredSlotId, @Nullable CompoundTag tag) {
        if(!isUnlocked(cosmetic.getId())) {
            return CosmeticResponse.of(ResponseType.LOCKED, "No tienes el cosmético desbloqueado.");
        }
        boolean equipped = false;
        if (preferredSlotId != null) {
            CosmeticSlot slot = slots.get(preferredSlotId);
            equipped = slot != null && slot.equip(cosmetic);
            if(equipped && tag != null) {
                slot.getEquipped().fromNbt(tag);
            }
        } else {
            // Auto: primer slot vacío de la categoría correcta
            for (CosmeticSlot slot : slots.values()) {
                if(equipped) break;
                if (slot.getCategoryId().equals(cosmetic.getCategoryId()) && slot.isEmpty()) {
                    equipped = slot.equip(cosmetic);
                    if(equipped && tag != null) {
                        slot.getEquipped().fromNbt(tag);
                    }
                }
            }
        }
        if(!equipped) {
            return CosmeticResponse.of(ResponseType.ERROR, String.format("Algo salió mal al equipar el cosmético %s.", cosmetic.getName()));
        }
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS, String.format("El cosmético %s fue equipado con éxito.", cosmetic.getName()));
    }

    public CosmeticResponse unEquip(Identifier slotId) {
        slots.get(slotId).clear();
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS, "El cosmético fue desequipado con éxito.");
    }

    public CosmeticResponse clearAll() {
        slots.values().forEach(CosmeticSlot::clear);
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS, "Se desequiparon todos los cosméticos con éxito.");
    }

    public boolean isUnlocked(Identifier id) {
        return unlockedCosmetics.contains(id);
    }

    public String showUnlockedCosmetics() {
        StringBuilder builder = new StringBuilder();
        for (Identifier unlockedCosmetic : unlockedCosmetics) {
            builder.append(String.format("%s\n", unlockedCosmetic));
        }
        return builder.toString();
    }

    public CosmeticResponse unlockAll() {
        for (CosmeticDefinition value : ServerCosmeticStore.INSTANCE.getAll().values()) {
            unlockedCosmetics.add(value.getId());
        }
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS, String.format("Se desbloquearon %s cosméticos.", ServerCosmeticStore.INSTANCE.getAll().size()));
    }

    public CosmeticResponse unlock(Identifier id) {
        if(!ServerCosmeticStore.INSTANCE.isValid(id)) {
            return CosmeticResponse.of(ResponseType.ERROR, "El 'ID' ingresado no corresponde a ningún cosmético.");
        }
        unlockedCosmetics.add(id);
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS, String.format("Se desbloqueó el cosmético %s con éxito.", ServerCosmeticStore.INSTANCE.get(id).getName()));
    }

    public String showEquipment() {
        return "TODO";
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonObject equipped = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        for (Identifier unlockedCosmetic : unlockedCosmetics) {
            jsonArray.add(unlockedCosmetic.toString());
        }

        for (Map.Entry<Identifier, CosmeticSlot> entry : slots.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                equipped.add(
                        entry.getKey().toString(),
                        entry.getValue().getEquipped().toJson()
                );
            }
        }

        json.add("unlocked", jsonArray);
        json.add("equipped", equipped);
        return json;
    }

    @Override
    public void fromJson(@NotNull JsonObject json) {
        unlockedCosmetics.clear();
        clearAll();

        if(json.has("unlocked") && json.get("unlocked").isJsonArray()) {
            JsonArray unlockedArray = json.getAsJsonArray("unlocked");
            for (JsonElement unlocked : unlockedArray) {
                unlockedCosmetics.add(Identifier.tryParse(unlocked.getAsString()));
            }
        }

        if(json.has("equipped")) {
            JsonObject equipped = json.getAsJsonObject("equipped");
            for (Map.Entry<String, JsonElement> entry : equipped.entrySet()) {
                Identifier slotId = Identifier.tryParse(entry.getKey());
                if (slotId == null) continue;

                CosmeticSlot slot = slots.get(slotId);
                if (slot == null) continue;

                try {
                    PlayerCosmeticData data = new PlayerCosmeticData(
                            ServerCosmeticStore.INSTANCE,
                            entry.getValue().getAsJsonObject()
                    );
                    // Validar que el cosmético cargado pertenece a la categoría del slot
                    if (!data.getCosmetic().getCategoryId().equals(slot.getCategoryId())) {
                        throw new Exception("Categoría incorrecta para slot '" + slotId + "'");
                    }
                    slot.setEquipped(data);
                } catch (Exception e) {
                    LOGGER.warn("Error cargando slot '{}': {}", slotId, e.getMessage());
                    slot.clear();
                }
            }
        }
    }
}
