package com.astradia.utils;

import com.astradia.api.player.CosmeticSlot;
import com.astradia.api.player.Slot;
import net.minecraft.util.Identifier;

public class SlotUtils {
    public static Slot[] getPlayerEquipmentSlots() {
        return new Slot[] {
                new CosmeticSlot(Identifier.of("head", "beard"), "head"),
                new CosmeticSlot(Identifier.of("head", "hair"), "head"),
                new CosmeticSlot(Identifier.of("head", "accessory"), "head"),
                new CosmeticSlot(Identifier.of("head", "accessory"), "head"),
                new CosmeticSlot(Identifier.of("head", "horns"), "head"),
                new CosmeticSlot(Identifier.of("head", "horns"), "head"),
                new CosmeticSlot(Identifier.of("head", "ears"), "head"),

                new CosmeticSlot(Identifier.of("torso", "accessory"), "torso"),
                new CosmeticSlot(Identifier.of("torso", "accessory"), "torso"),
                new CosmeticSlot(Identifier.of("torso", "tail"), "torso"),

                new CosmeticSlot(Identifier.of("left_arm", "replace"), "left_arm"),
                new CosmeticSlot(Identifier.of("left_arm", "claws"), "left_arm"),
                new CosmeticSlot(Identifier.of("left_arm", "wings"), "left_arm"),

                //new EquipmentSlot(BodyPart.RIGHT_ARM, SlotType.REPLACE),
                //new EquipmentSlot(BodyPart.RIGHT_ARM, SlotType.CLAWS),
                //new EquipmentSlot(BodyPart.RIGHT_ARM, SlotType.WINGS),

                new CosmeticSlot(Identifier.of("left_leg", "replace"), "left_leg"),
                new CosmeticSlot(Identifier.of("left_leg", "claws"), "left_leg"),

                //new EquipmentSlot(BodyPart.RIGHT_LEG, SlotType.REPLACE),
                //new EquipmentSlot(BodyPart.RIGHT_LEG, SlotType.CLAWS),
        };
    }
}
