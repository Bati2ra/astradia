package com.astradia.enums;

import net.minecraft.util.StringIdentifiable;

public enum BodyPart implements StringIdentifiable {
    HEAD("head", new SlotType[] {
            SlotType.BEARD,
            SlotType.HAIR,
            SlotType.ACCESSORY,
            SlotType.ACCESSORY,
            SlotType.HORNS,
            SlotType.HORNS,
            SlotType.EARS
    }),
    TORSO("torso", new SlotType[] {
            SlotType.ACCESSORY,
            SlotType.ACCESSORY,
            SlotType.TAIL
    }),
    LEFT_ARM("leftarm", new SlotType[] {
            SlotType.REPLACE,
            SlotType.CLAWS,
            SlotType.WINGS
    }),
    RIGHT_ARM("rightarm", new SlotType[] {
            SlotType.REPLACE,
            SlotType.CLAWS,
            SlotType.WINGS
    }),
    LEFT_LEG("leftleg", new SlotType[] {
            SlotType.REPLACE,
            SlotType.CLAWS
    }),
    RIGHT_LEG("rightleg", new SlotType[] {
            SlotType.REPLACE,
            SlotType.CLAWS
    });

    private final SlotType[] slots;
    private final String name;

    BodyPart(String name, SlotType[] slots) {
        this.slots = slots;
        this.name = name;
    }

    public SlotType[] getSlots() {
        return slots;
    }

    public String getName() {
        return name;
    }

    @Override
    public String asString() {
        return name;
    }
}
