package com.astradia.api.player;

import com.astradia.api.CosmeticCategory;
import com.astradia.api.CosmeticDefinition;
import com.astradia.api.CosmeticCategoryRegistry;
import net.minecraft.resources.Identifier;

public class CosmeticSlot {
    private final Identifier id;           // "head:horns"
    private final Identifier categoryId;   // "cosmetics:horns"

    private PlayerCosmeticData equipped;

    public CosmeticSlot(Identifier id, Identifier categoryId) {
        this.id = id;
        this.categoryId = categoryId;
    }

    public boolean equip(CosmeticDefinition cosmetic) {
        CosmeticCategory category = CosmeticCategoryRegistry.get(cosmetic.getCategoryId());
        if (category == null) return false;

        if (!cosmetic.getCategoryId().equals(this.categoryId)) return false;

        // El slot debe estar en los slots disponibles de esa categoría
        if (!category.getSlots().contains(this.id)) return false;

        this.equipped = new PlayerCosmeticData(cosmetic);
        return true;
    }

    public void clear() {
        equipped = null;
    }

    public boolean isEmpty() {
        return equipped == null;
    }

    public Identifier getId() {
        return id;
    }

    public Identifier getCategoryId() {
        return categoryId;
    }

    public PlayerCosmeticData getEquipped() {
        return equipped;
    }

    public void setEquipped(PlayerCosmeticData equipped) {
        this.equipped = equipped;
    }
}
