package com.astradia.player;

import com.astradia.ClientCosmeticStore;
import com.astradia.enums.SlotType;
import com.astradia.pojo.ClientCosmetic;

public class ClientEquipmentSlot extends EquipmentSlot {
    private final CachedCosmetic cachedCosmetic;

    public ClientEquipmentSlot(SlotType slotType) {
        super(slotType);
        cachedCosmetic = new CachedCosmetic();
    }

    public CachedCosmetic getCachedCosmetic() {
        return cachedCosmetic;
    }

    public static class CachedCosmetic {
        private Integer id;
        private ClientCosmetic cached;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public ClientCosmetic getCached() {
            if(cached == null) {
                cached = ClientCosmeticStore.INSTANCE.get(id);
            }
            return cached;
        }
    }
}
