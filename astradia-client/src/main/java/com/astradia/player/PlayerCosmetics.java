package com.astradia.player;

import com.astradia.ClientCosmeticStore;
import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import com.astradia.pojo.ClientCosmetic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.UUID;

public class PlayerCosmetics extends PlayerFeature {

    protected final EquipmentSlot[] equippedInventory = new EquipmentSlot[] {
                new EquipmentSlot(BodyPart.HEAD, SlotType.BEARD),
                new EquipmentSlot(BodyPart.HEAD, SlotType.HAIR),
                new EquipmentSlot(BodyPart.HEAD, SlotType.ACCESSORY),
                new EquipmentSlot(BodyPart.HEAD, SlotType.ACCESSORY),
                new EquipmentSlot(BodyPart.HEAD, SlotType.HORNS),
                new EquipmentSlot(BodyPart.HEAD, SlotType.HORNS),
                new EquipmentSlot(BodyPart.HEAD, SlotType.EARS),

                new EquipmentSlot(BodyPart.TORSO, SlotType.ACCESSORY),
                new EquipmentSlot(BodyPart.TORSO, SlotType.ACCESSORY),
                new EquipmentSlot(BodyPart.TORSO, SlotType.TAIL),

                new EquipmentSlot(BodyPart.LEFT_ARM, SlotType.REPLACE),
                new EquipmentSlot(BodyPart.LEFT_ARM, SlotType.CLAWS),
                new EquipmentSlot(BodyPart.LEFT_ARM, SlotType.WINGS),

                new EquipmentSlot(BodyPart.RIGHT_ARM, SlotType.REPLACE),
                new EquipmentSlot(BodyPart.RIGHT_ARM, SlotType.CLAWS),
                new EquipmentSlot(BodyPart.RIGHT_ARM, SlotType.WINGS),

                new EquipmentSlot(BodyPart.LEFT_LEG, SlotType.REPLACE),
                new EquipmentSlot(BodyPart.LEFT_LEG, SlotType.CLAWS),

                new EquipmentSlot(BodyPart.RIGHT_LEG, SlotType.REPLACE),
                new EquipmentSlot(BodyPart.RIGHT_LEG, SlotType.CLAWS),
    };

    public PlayerCosmetics(UUID playerId) {
        super("cosmetics", playerId);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        NbtList list = (NbtList) tag.get("equipped");
        for (NbtElement nbtElement : list) {
            if(!(nbtElement instanceof NbtCompound nbtCompound)) continue;
            int slotId = nbtCompound.getByte("slot");
            int id = nbtCompound.getInt("id");
            if(!equippedInventory[slotId].equip(id)) {
                continue;
            }
            if(nbtCompound.contains("data")) {
                equippedInventory[slotId].setStoredData(nbtCompound.getCompound("data"));
            }
        }
    }

    public EquipmentSlot[] getEquippedInventory() {
        return equippedInventory;
    }
}
