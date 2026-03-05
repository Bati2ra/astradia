package com.astradia.api;

import net.minecraft.resources.Identifier;

import java.util.List;

public class CosmeticCategory {
    private final Identifier id;           // "cosmetics:horns"
    private final String displayName;      // "Horns"
    private final String bodySection;  // "head", "torso", "arms", "legs"
    private final int maxEquipped;
    private final List<Identifier> slots;

    public CosmeticCategory(Identifier id, String displayName, List<Identifier> slots) {
        this.id = id;
        this.displayName = displayName;
        this.bodySection = id.getNamespace();
        this.slots = slots;
        maxEquipped = slots.size();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBodySection() {
        return bodySection;
    }

    public Identifier getId() {
        return id;
    }

    public int getMaxEquipped() {
        return maxEquipped;
    }

    public List<Identifier> getSlots() {
        return slots;
    }
}