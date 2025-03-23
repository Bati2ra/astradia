package com.astradia.player;

import com.astradia.enums.SlotType;
import net.minecraft.nbt.NbtCompound;

public abstract class EquipmentSlot {
    private final SlotType slotType;
    protected NbtCompound storedData;

    public EquipmentSlot(SlotType slotType) {
        this.slotType = slotType;
    }

    public void setStoredData(NbtCompound compound) {
        this.storedData = compound;
    }

    public NbtCompound getStoredData() {
        return storedData;
    }

    public SlotType getSlotType() {
        return slotType;
    }

}
