package com.astradia.api;

import com.astradia.api.player.CosmeticSlot;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class CosmeticCategoryRegistry {
    public static final Identifier HEAD_HORNS = Identifier.fromNamespaceAndPath("head", "horns");
    public static final Identifier HEAD_HAIR = Identifier.fromNamespaceAndPath("head", "hair");
    public static final Identifier HEAD_BEARD = Identifier.fromNamespaceAndPath("head", "beard");
    public static final Identifier HEAD_ACCESSORY = Identifier.fromNamespaceAndPath("head", "accessory");
    public static final Identifier HEAD_EARS = Identifier.fromNamespaceAndPath("head", "ears");

    public static final Identifier TORSO_ACCESSORY = Identifier.fromNamespaceAndPath("torso", "accessory");
    public static final Identifier TORSO_TAIL = Identifier.fromNamespaceAndPath("torso", "tail");

    public static final Identifier ARMS_CLAWS = Identifier.fromNamespaceAndPath("arms", "claws");
    public static final Identifier ARMS_REPLACE = Identifier.fromNamespaceAndPath("arms", "replace");
    public static final Identifier ARMS_WINGS = Identifier.fromNamespaceAndPath("arms", "wings");

    public static final Identifier LEGS_REPLACE = Identifier.fromNamespaceAndPath("legs", "replace");
    public static final Identifier LEGS_CLAWS = Identifier.fromNamespaceAndPath("legs", "claws");

    private static final HashMap<Identifier, CosmeticCategory> CATEGORIES = new HashMap<>();

    public static Map<Identifier, CosmeticSlot> buildSlotsFromCategories() {
        return buildSlotsFromCategories(CosmeticSlot::new);
    }

    public static <T extends CosmeticSlot> Map<Identifier, T> buildSlotsFromCategories(
            BiFunction<Identifier, Identifier, T> slotFactory
    ) {
        Map<Identifier, T> slots = new HashMap<>();

        for (CosmeticCategory category : CATEGORIES.values()) {
            for (Identifier slotId : category.getSlots()) {
                slots.put(slotId, slotFactory.apply(slotId, category.getId()));
            }
        }

        return slots;
    }

    public static CosmeticCategory get(Identifier categoryId) {
        return CATEGORIES.get(categoryId);
    }

    static {
        CATEGORIES.put(HEAD_HORNS,
                new CosmeticCategory(HEAD_HORNS, "Horns", List.of(
                        Identifier.fromNamespaceAndPath("head", "horns"),
                        Identifier.fromNamespaceAndPath("head", "secondary_horns")
                )));
        CATEGORIES.put(HEAD_HAIR,
                new CosmeticCategory(HEAD_HAIR, "Hair", List.of(
                        Identifier.fromNamespaceAndPath("head", "hair")
                )));
        CATEGORIES.put(HEAD_BEARD,
                new CosmeticCategory(HEAD_BEARD, "Beard", List.of(
                        Identifier.fromNamespaceAndPath("head", "beard")
                )));
        CATEGORIES.put(HEAD_ACCESSORY,
                new CosmeticCategory(HEAD_ACCESSORY, "Accessory", List.of(
                        Identifier.fromNamespaceAndPath("head", "accessory"),
                        Identifier.fromNamespaceAndPath("head", "secondary_accessory")
                )));
        CATEGORIES.put(HEAD_EARS,
                new CosmeticCategory(HEAD_EARS, "Ears", List.of(
                        Identifier.fromNamespaceAndPath("head", "ears")
                )));
        CATEGORIES.put(TORSO_ACCESSORY,
                new CosmeticCategory(TORSO_ACCESSORY, "Accessory", List.of(
                        Identifier.fromNamespaceAndPath("torso", "accessory"),
                        Identifier.fromNamespaceAndPath("torso", "secondary_accessory")
                )));
        CATEGORIES.put(TORSO_TAIL,
                new CosmeticCategory(TORSO_TAIL, "Tail", List.of(
                        Identifier.fromNamespaceAndPath("torso", "tail")
                )));
        CATEGORIES.put(ARMS_CLAWS,
                new CosmeticCategory(ARMS_CLAWS, "Claws", List.of(
                        Identifier.fromNamespaceAndPath("arms", "claws")
                )));
        CATEGORIES.put(ARMS_REPLACE,
                new CosmeticCategory(ARMS_REPLACE, "Replace", List.of(
                        Identifier.fromNamespaceAndPath("arms", "replace")
                )));
        CATEGORIES.put(ARMS_WINGS,
                new CosmeticCategory(ARMS_WINGS, "Wings", List.of(
                        Identifier.fromNamespaceAndPath("arms", "wings")
                )));
        CATEGORIES.put(LEGS_CLAWS,
                new CosmeticCategory(LEGS_CLAWS, "Claws", List.of(
                        Identifier.fromNamespaceAndPath("legs", "claws")
                )));
        CATEGORIES.put(LEGS_REPLACE,
                new CosmeticCategory(LEGS_REPLACE, "Replace", List.of(
                        Identifier.fromNamespaceAndPath("legs", "replace")
                )));
    }
}
