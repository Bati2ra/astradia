package com.astradia.api;

public enum CosmeticRarity {
    COMMON("common", 0xAAAAAA),
    UNCOMMON("uncommon", 0x55AA55),
    RARE("rare", 0x5555FF),
    EPIC("epic", 0xAA00AA),
    LEGENDARY("legendary", 0xFFAA00),
    MYTHIC("mythic", 0xFF5555);

    private final String id;
    private final int color;

    CosmeticRarity(String id, int color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public int getColor() {
        return color;
    }
}