package com.astradia.enums;

import net.minecraft.util.StringIdentifiable;

public enum SlotType implements StringIdentifiable {
    BEARD("beard"),
    HAIR("hair"),
    ACCESSORY("accessory"),
    HORNS("horns"),
    EARS("ears"),
    REPLACE("replace"),
    CLAWS("claws"),
    WINGS("wings"),
    TAIL("tail");

    private final String name;
    SlotType(String name) {
        this.name = name;
    }
    @Override
    public String asString() {
        return name;
    }
}
