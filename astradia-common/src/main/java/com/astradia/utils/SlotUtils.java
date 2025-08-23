package com.astradia.utils;

import com.astradia.api.player.CosmeticSlot;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SlotUtils {
    public static Map<Identifier, CosmeticSlot> getPlayerEquipmentSlots() {
        Map<Identifier, CosmeticSlot> slots = new HashMap<>();

        slots.put(Identifier.of("head", "beard"), new CosmeticSlot(Identifier.of("head", "beard"), "head"));
        slots.put(Identifier.of("head", "hair"), new CosmeticSlot(Identifier.of("head", "hair"), "head"));
        slots.put(Identifier.of("head", "accessory"), new CosmeticSlot(Identifier.of("head", "accessory"), "head"));
        slots.put(Identifier.of("head", "secondary_accessory"), new CosmeticSlot(Identifier.of("head", "accessory"), "head"));
        slots.put(Identifier.of("head", "horns"), new CosmeticSlot(Identifier.of("head", "horns"), "head"));
        slots.put(Identifier.of("head", "secondary_horns"), new CosmeticSlot(Identifier.of("head", "horns"), "head"));
        slots.put(Identifier.of("head", "ears"), new CosmeticSlot(Identifier.of("head", "ears"), "head"));

        slots.put(Identifier.of("torso", "accessory"), new CosmeticSlot(Identifier.of("torso", "accessory"), "torso"));
        slots.put(Identifier.of("torso", "secondary_accessory"), new CosmeticSlot(Identifier.of("torso", "accessory"), "torso"));
        slots.put(Identifier.of("torso", "tail"), new CosmeticSlot(Identifier.of("torso", "tail"), "torso"));

        slots.put(Identifier.of("arms", "replace"), new CosmeticSlot(Identifier.of("arms", "replace"), "arms"));
        slots.put(Identifier.of("arms", "claws"), new CosmeticSlot(Identifier.of("arms", "claws"), "arms"));
        slots.put(Identifier.of("arms", "wings"), new CosmeticSlot(Identifier.of("arms", "wings"), "arms"));

        slots.put(Identifier.of("legs", "replace"), new CosmeticSlot(Identifier.of("legs", "replace"), "legs"));
        slots.put(Identifier.of("legs", "claws"), new CosmeticSlot(Identifier.of("legs", "claws"), "legs"));

        return slots;
    }
}
