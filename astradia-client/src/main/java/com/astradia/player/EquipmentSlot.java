package com.astradia.player;

import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import com.astradia.pojo.ClientCosmetic;
import com.astradia.pojo.Cosmetic;
import net.minecraft.nbt.NbtCompound;

public class EquipmentSlot {
    private final SlotType slotType;
    private final BodyPart bodyPart;

    private ClientCosmetic cosmetic;
    protected NbtCompound storedData;

    public EquipmentSlot(BodyPart bodyPart, SlotType slotType) {
        this.slotType = slotType;
        this.bodyPart = bodyPart;
    }

    public boolean equip(ClientCosmetic cosmetic) {
        if(canBeEquipped(cosmetic)) {
            this.storedData = new NbtCompound();
            this.cosmetic = cosmetic;
            return true;
        }
        return false;
    }

    public void unequip() {
        this.storedData = null;
        this.cosmetic = null;
    }

    private boolean canBeEquipped(ClientCosmetic cosmetic) {
        return bodyPart.equals(cosmetic.getBodyPart()) && slotType.equals(cosmetic.getSlotType());
    }

    public ClientCosmetic getCosmetic() {
        return cosmetic;
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

    public BodyPart getBodyPart() {
        return bodyPart;
    }
}
