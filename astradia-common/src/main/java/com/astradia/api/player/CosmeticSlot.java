package com.astradia.api.player;

import com.astradia.api.CosmeticDefinition;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CosmeticSlot extends Slot {
    private PlayerCosmeticData cosmeticData;

    public CosmeticSlot(Identifier name) {
        super(name);
    }

    public CosmeticSlot(Identifier name, String category) {
        super(name, category);
    }

    public boolean equip(CosmeticDefinition cosmetic, @Nullable String variantId) {
        if(cosmetic.getSlotId().equals(this.getName())) {
            cosmeticData = new PlayerCosmeticData(cosmetic, variantId == null ? "default" : variantId);
            return true;
        }
        return false;
    }

    public void clear() {
        cosmeticData = null;
    }

    public PlayerCosmeticData getCosmeticData() {
        return cosmeticData;
    }

    public void setCosmeticData(PlayerCosmeticData cosmeticData) {
        this.cosmeticData = cosmeticData;
    }
}
