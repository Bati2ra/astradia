package com.astradia.api.player;

import com.astradia.api.CosmeticInfo;
import net.minecraft.util.Identifier;

public class CosmeticSlot extends Slot {
    private PlayerCosmeticData cosmeticData;

    public CosmeticSlot(Identifier name) {
        super(name);
    }

    public CosmeticSlot(Identifier name, String category) {
        super(name, category);
    }

    public boolean equip(CosmeticInfo cosmetic) {
        if(cosmetic.getSlotId().equals(this.getName())) {
            cosmeticData = new PlayerCosmeticData(cosmetic);
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
