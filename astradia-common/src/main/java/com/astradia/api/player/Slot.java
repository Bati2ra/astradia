package com.astradia.api.player;

import com.astradia.api.CosmeticInfo;
import net.minecraft.util.Identifier;

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

    public boolean equip(CosmeticInfo cosmetic) {
        return false;
    }
}
