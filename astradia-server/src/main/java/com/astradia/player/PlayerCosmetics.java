package com.astradia.player;

import com.astradia.ServerCosmeticStore;
import com.astradia.api.player.CosmeticSlot;
import com.astradia.api.player.PlayerCosmeticData;
import com.astradia.enums.ResponseType;
import com.astradia.api.CosmeticInfo;
import com.astradia.utils.CosmeticResponse;
import com.astradia.utils.SlotUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerCosmetics extends PlayerFeature {
    protected final HashSet<Integer> unlockedCosmetics = new HashSet<>();

    protected final Map<Identifier, CosmeticSlot> equippedInventory = SlotUtils.getPlayerEquipmentSlots();

    public PlayerCosmetics(UUID playerId) {
        super("cosmetics", playerId);
    }

    public CosmeticResponse equipCosmetic(Identifier slotId, CosmeticInfo cosmetic, @Nullable NbtCompound nbt) {
        if(!isUnlocked(cosmetic.getId())) return CosmeticResponse.of(ResponseType.LOCKED);
        boolean wasEquipped = equippedInventory.get(slotId).equip(cosmetic);
        if(!wasEquipped) return CosmeticResponse.of(ResponseType.ERROR);
        if(nbt != null) {
            // TODO equippedInventory[slotId].getCosmeticData().getTypeData(AnimatableType.PlayerData.class);
        }
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS);
    }

    public CosmeticResponse unequipCosmetic(Identifier slotId) {
        equippedInventory.get(slotId).clear();
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS);
    }

    public CosmeticResponse clearSlots() {
        for (CosmeticSlot slot : equippedInventory.values()) {
            slot.clear();
        }
        return CosmeticResponse.of(ResponseType.SUCCESS);
    }

    public boolean isUnlocked(Integer id) {
        return unlockedCosmetics.contains(id);
    }

    public String showUnlockedCosmetics() {
        StringBuilder builder = new StringBuilder();
        for (Integer unlockedCosmetic : unlockedCosmetics) {
            builder.append(String.format("%s\n", unlockedCosmetic));
        }
        return builder.toString();
    }

    public CosmeticResponse unlockAll() {
        for (CosmeticInfo value : ServerCosmeticStore.INSTANCE.getAll().values()) {
            unlockedCosmetics.add(value.getId());
        }
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS);
    }

    public CosmeticResponse unlock(Integer id) {
        if(!ServerCosmeticStore.INSTANCE.isValid(id)) {
            return CosmeticResponse.of(ResponseType.ERROR, "El 'ID' ingresado no corresponde a ningún cosmético.");
        }
        unlockedCosmetics.add(id);
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS);
    }

    public String showEquipment() {
        StringBuilder builder = new StringBuilder();
        for (CosmeticSlot value : equippedInventory.values()) {
            builder.append(String.format("Slot - %s - %s", value.getName().toString(), value.getCategory()));
            builder.append(String.format("  - %s", value.getCosmeticData() != null ? value.getCosmeticData().getCosmetic().getName() : "Vacío"));
        }
        return builder.toString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonObject jsonEquipped = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (Integer unlockedCosmetic : unlockedCosmetics) {
            jsonArray.add(unlockedCosmetic);
        }
        jsonObject.add("unlocked", jsonArray);

        for (Map.Entry<Identifier, CosmeticSlot> entry : equippedInventory.entrySet()) {
            PlayerCosmeticData data = entry.getValue().getCosmeticData();
            if(data == null) continue;
            jsonEquipped.add(entry.getKey().toString(), data.toJson());
        }
        jsonObject.add("equipped", jsonEquipped);
        return jsonObject;
    }

    @Override
    public void fromJson(@NotNull JsonObject json) {
        unlockedCosmetics.clear();
        clearSlots();

        if(json.has("unlocked") && json.get("unlocked").isJsonArray()) {
            JsonArray unlockedArray = json.getAsJsonArray("unlocked");
            for (JsonElement unlocked : unlockedArray) {
                unlockedCosmetics.add(unlocked.getAsInt());
            }
        }
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
                    slot.setCosmeticData(new PlayerCosmeticData(ServerCosmeticStore.INSTANCE, entry.getValue().getAsJsonObject()));
                } catch (Exception e) {
                    e.printStackTrace();
                    slot.clear();
                }
            }
        }
    }
}
