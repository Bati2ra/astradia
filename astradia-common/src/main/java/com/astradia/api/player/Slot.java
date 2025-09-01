package com.astradia.api.player;

import com.astradia.api.CosmeticDefinition;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class Slot {
    private final Identifier name;
    private String category;

    public Slot(Identifier name) {
        this.name = name;
    }

    public Slot(Identifier name, String category) {
        this.name = name;
        this.category =category;
    }

    public Identifier getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public boolean equip(CosmeticDefinition cosmetic, @Nullable String variantId) {
        return false;
    }

    public boolean equip(CosmeticDefinition cosmetic) {
        return equip(cosmetic, null);
    }
}
