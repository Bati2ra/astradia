package com.astradia.player;

import com.astradia.pojo.Cosmetic;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
/*
public class CosmeticData extends EPlayerData.DataComponent {
    private final HashSet<Integer> unlockedCosmetics;
    private final HashMap<String, List<CosmeticEquipmentData>> equipment;

    public CosmeticData(EPlayerData data) {
        super(data);
        unlockedCosmetics = new HashSet<>();
        equipment = new HashMap<>();
    }
    @Override
    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        NbtCompound equipmentTag = new NbtCompound();
        for (Map.Entry<String, List<CosmeticEquipmentData>> equipmentDataEntry : equipment.entrySet()) {
            NbtCompound partTag = new NbtCompound();
            for (CosmeticEquipmentData cosmeticEquipmentData : equipmentDataEntry.getValue()) {
                if(cosmeticEquipmentData.getCosmetic() == null) continue;
                NbtCompound cosmeticEquipmentTag = new NbtCompound();
                cosmeticEquipmentTag.putInt("id", cosmeticEquipmentData.getCosmetic().getId());
                if(cosmeticEquipmentData.getStored() != null)
                    cosmeticEquipmentTag.put("stored", cosmeticEquipmentData.getStored());
                partTag.put(cosmeticEquipmentData.getSlotName(), cosmeticEquipmentTag);
            }
            equipmentTag.put(equipmentDataEntry.getKey(), partTag);
        }
        tag.put("equipment", equipmentTag);
        return tag;
    }

    @Override
    public void fromNbt(NbtCompound tag) {
        NbtCompound equipmentTag = tag.getCompound("equipment");
        for (String key : equipmentTag.getKeys()) {
            NbtCompound partTag = tag.getCompound(key);
            for (String partTagKey : partTag.getKeys()) {
                NbtCompound equipmentData = partTag.getCompound(partTagKey);
                // TODO
            }
        }
    }

    public HashSet<Integer> getUnlockedCosmetics() {
        return unlockedCosmetics;
    }

    public List<CosmeticEquipmentData> getEquipmentSlots(String name) {
        return equipment.get(name);
    }

    public static class CosmeticEquipmentData {
        private final String slotName;
        private Cosmetic cosmetic;
        private NbtCompound stored;

        public CosmeticEquipmentData(String name) {
            slotName = name;
        }

        public void setStored(NbtCompound stored) {
            this.stored = stored;
        }

        public void equip(Cosmetic cosmetic) {
            this.stored = new NbtCompound();
            this.cosmetic = cosmetic;
        }

        public void unequip() {
            this.stored = null;
            this.cosmetic = null;
        }

        public Cosmetic getCosmetic() {
            return cosmetic;
        }

        public NbtCompound getStored() {
            return stored;
        }

        public String getSlotName() {
            return slotName;
        }
    }
}
*/