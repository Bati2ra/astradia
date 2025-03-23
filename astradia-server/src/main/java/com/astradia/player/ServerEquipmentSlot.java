package com.astradia.player;

import com.astradia.enums.SlotType;
import com.astradia.pojo.Cosmetic;
import net.minecraft.nbt.NbtCompound;

public class ServerEquipmentSlot extends EquipmentSlot {
    private Cosmetic equippedCosmetic;

    public ServerEquipmentSlot(SlotType slotType) {
        super(slotType);
    }

    public void equip(Cosmetic cosmetic) {
        this.storedData = new NbtCompound();
        this.equippedCosmetic = cosmetic;
    }

    public void unequip() {
        this.storedData = null;
        this.equippedCosmetic = null;
    }

    public Cosmetic getEquippedCosmetic() {
        return equippedCosmetic;
    }
}
