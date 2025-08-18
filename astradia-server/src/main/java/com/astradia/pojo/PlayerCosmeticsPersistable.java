package com.astradia.pojo;

import java.util.HashSet;
import java.util.Set;

public class PlayerCosmeticsPersistable {
    public Set<Integer> unlockedCosmetics = new HashSet<>();
    public EquipmentSlotPersistable[] equippedInventory = new EquipmentSlotPersistable[0];

    public static class EquipmentSlotPersistable {
        public int slotId;
        public int cosmeticId;
        public String storedData;
    }
}
