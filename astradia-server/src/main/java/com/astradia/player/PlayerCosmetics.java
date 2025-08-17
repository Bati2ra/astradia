package com.astradia.player;

import com.astradia.AstradiaServer;
import com.astradia.ServerCosmeticStore;
import com.astradia.enums.ResponseType;
import com.astradia.pojo.Cosmetic;
import com.astradia.utils.CosmeticResponse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerCosmetics extends PlayerFeature {
    protected final HashSet<Integer> unlockedCosmetics = new HashSet<>();

    protected final EquipmentSlot[] equippedInventory = AstradiaServer.getEquipmentSlots();

    public PlayerCosmetics(UUID playerId) {
        super("cosmetics", playerId);
    }

    public CosmeticResponse equipCosmetic(int slotId, Cosmetic cosmetic, @Nullable NbtCompound nbt) {
        if(!isUnlocked(cosmetic.getId())) return CosmeticResponse.of(ResponseType.LOCKED);
        boolean wasEquipped = equippedInventory[slotId].equip(cosmetic);
        if(!wasEquipped) return CosmeticResponse.of(ResponseType.ERROR);
        if(nbt != null) {
            equippedInventory[slotId].setStoredData(nbt);
        }
        isDirty = true;
        return CosmeticResponse.of(ResponseType.SUCCESS);
    }

    public CosmeticResponse unequipCosmetic(int slotId) {
        equippedInventory[slotId].unequip();
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


    @Override
    public void sync(PlayerEntity player) {
        isDirty = false;
    }

    public CosmeticResponse unlockAll() {
        for (Cosmetic value : ServerCosmeticStore.INSTANCE.getAll().values()) {
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
        for (int i = 0; i < equippedInventory.length; i++) {
            EquipmentSlot slot = equippedInventory[i];
            builder.append(String.format("Slot #%s - %s - %s", i, slot.getBodyPart().name(), slot.getSlotType().name()));
            builder.append(String.format("  - %s", slot.getCosmetic() != null ? slot.getCosmetic().getName() : "Vacío"));
        }
        return builder.toString();
    }

    @Override
    public NbtCompound serialize() {
        NbtCompound tag = new NbtCompound();
        NbtList list = new NbtList();
        for (int i = 0; i < equippedInventory.length; i++) {
            EquipmentSlot slot = equippedInventory[i];
            if(slot.getCosmetic() == null) continue;
            NbtCompound cosmeticData = new NbtCompound();
            cosmeticData.putByte("slot", (byte)i);
            cosmeticData.putInt("id", slot.getCosmetic().getId());
            if(slot.getStoredData() != null) {
                cosmeticData.put("data", slot.getStoredData());
            }
            list.add(cosmeticData);
        }
        tag.put("equipped", list);
        return tag;
    }

    @Override
    public void deserialize(NbtCompound tag) {

    }
}
