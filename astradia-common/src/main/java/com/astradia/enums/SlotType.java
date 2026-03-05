package com.astradia.enums;

import net.minecraft.util.StringRepresentable;

public enum SlotType implements StringRepresentable {
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
    public String getSerializedName() {
        return name;
    }
}
