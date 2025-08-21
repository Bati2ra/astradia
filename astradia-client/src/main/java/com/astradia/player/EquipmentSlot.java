package com.astradia.player;

import com.astradia.ClientCosmeticStore;
import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import com.astradia.pojo.ClientCosmetic;
import net.minecraft.nbt.NbtCompound;

public class EquipmentSlot {
    private final SlotType slotType;
    private final BodyPart bodyPart;

    private ClientCosmetic cachedCosmetic;
    private Integer cosmeticId;
    protected NbtCompound storedData;

    public EquipmentSlot(BodyPart bodyPart, SlotType slotType) {
        this.slotType = slotType;
        this.bodyPart = bodyPart;
        cosmeticId = null;
    }

    public boolean equip(int cosmeticId) {
        this.storedData = new NbtCompound();
        this.cosmeticId = cosmeticId;
        return true;
    }

    public void unequip() {
        this.storedData = null;
        this.cosmeticId = null;
    }

    private boolean canBeEquipped(ClientCosmetic cosmetic) {
        return bodyPart.equals(cosmetic.getBodyPart()) && slotType.equals(cosmetic.getSlotType());
    }

    public ClientCosmetic getCachedCosmetic() {
        if(cosmeticId != null && cachedCosmetic == null) {
            cachedCosmetic = ClientCosmeticStore.INSTANCE.get(cosmeticId);
        }
        return cachedCosmetic;
    }

    public Integer getCosmeticId() {
        return cosmeticId;
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
