package com.astradia;

import com.astradia.enums.BodyPart;
import com.astradia.enums.ResponseType;
import com.astradia.enums.SlotType;
import com.astradia.player.ServerEquipmentSlot;
import com.astradia.pojo.Cosmetic;
import com.astradia.utils.CosmeticResponse;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServerPlayerCosmetics extends PlayerCosmetics<ServerEquipmentSlot> {
    protected ServerPlayerCosmetics(UUID uuid) {
        super(uuid);
    }

    @Override
    protected ServerEquipmentSlot addSlot(SlotType slotType) {
        return new ServerEquipmentSlot(slotType);
    }

    @Override
    public void fromNbt(NbtCompound tag) {

    }

    public CosmeticResponse unlockAll() {
        for (Cosmetic value : ServerCosmeticStore.INSTANCE.getAll().values()) {
            unlockedCosmetics.add(value.getId());
        }
        return CosmeticResponse.of(ResponseType.SUCCESS);
    }
    public CosmeticResponse unlock(Integer id) {
        if(!ServerCosmeticStore.INSTANCE.isValid(id)) {
            return CosmeticResponse.of(ResponseType.ERROR, "El 'ID' ingresado no corresponde a ningún cosmético.");
        }
        unlockedCosmetics.add(id);
        return CosmeticResponse.of(ResponseType.SUCCESS);
    }

    public CosmeticResponse equipItem(Cosmetic item, int slotIndex) {
        return equipItem(item, item.getBodyPart(), slotIndex, null);
    }

    public CosmeticResponse equipItem(Cosmetic item, BodyPart bodyPart, int slotIndex) {
        return equipItem(item, bodyPart, slotIndex, null);
    }

    public CosmeticResponse equipItem(Cosmetic item, BodyPart bodyPart, int slotIndex, @Nullable  NbtCompound nbt) {
        if(!isUnlocked(item.getId())) return CosmeticResponse.of(ResponseType.LOCKED, "Cosmético no desbloqueado.");

        List<ServerEquipmentSlot> slots = equipment.get(bodyPart);
        if (slots != null) {
            ServerEquipmentSlot equipmentSlot = slots.get(Math.clamp(slotIndex, 0, slots.size() - 1));
            if(equipmentSlot.getSlotType().equals(item.getSlotType())) {
                equipmentSlot.equip(item);
                if(nbt != null)
                    equipmentSlot.setStoredData(nbt);
                return CosmeticResponse.of(ResponseType.SUCCESS);
            }
        }
        return CosmeticResponse.of(ResponseType.ERROR, "El 'SLOT' ó 'PART' no es válido.");
    }

    public CosmeticResponse unequipItem(BodyPart part, int slotIndex) {
        List<ServerEquipmentSlot> slots = equipment.get(part);
        if (slots != null) {
            ServerEquipmentSlot equipmentSlot = slots.get(Math.clamp(slotIndex, 0, slots.size() - 1));
            equipmentSlot.unequip();
            return CosmeticResponse.of(ResponseType.SUCCESS);
        }
        return CosmeticResponse.of(ResponseType.ERROR, "El 'SLOT' ó 'PART' no es válido.");
    }
    /*
    Refactorizar más adelante, cuando los cosméticos guarden información extra.
     */
    @Override
    public NbtCompound toNbt() {
        var iterator = equipment.entrySet().stream().iterator();
        NbtCompound tag = new NbtCompound();
        NbtCompound cosmeticsTag = new NbtCompound();
        tag.putUuid("uuid", uuid);
        while(iterator.hasNext()) {
            var entry = iterator.next();
            var bodyPart = entry.getKey();
            var equipmentSlots = entry.getValue();
            NbtList list = new NbtList();
            for (int i = 0; i < equipmentSlots.size(); i++) {
                var equipmentSlot = equipmentSlots.get(i);
                if(equipmentSlot.getEquippedCosmetic() == null) continue;
                NbtCompound equipmentTag = new NbtCompound();
                equipmentTag.putByte("_", (byte) i);
                equipmentTag.putInt("id", equipmentSlot.getEquippedCosmetic().getId());
                if(equipmentSlot.getStoredData() != null)
                    equipmentTag.put("data", equipmentSlot.getStoredData());
                list.add(equipmentTag);
            }
            cosmeticsTag.put(bodyPart.name(), list);
        }
        tag.put("cosmetics", cosmeticsTag);
        return tag;
    }

    public String showEquipment() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<BodyPart, List<ServerEquipmentSlot>> entry : equipment.entrySet()) {
            builder.append(String.format("Parte del cuerpo: %s\n", entry.getKey()));
            for (ServerEquipmentSlot slot : entry.getValue()) {
                String itemName = slot.getEquippedCosmetic() != null ? slot.getEquippedCosmetic().getName() : "Vacío";
                builder.append(String.format("  - %s: %s\n", slot.getSlotType(), itemName));
            }
        }
        return builder.toString();
    }
}
