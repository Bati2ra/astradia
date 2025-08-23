package com.astradia.api.player;

import com.astradia.api.CosmeticInfo;
import com.astradia.api.CosmeticProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerCosmeticData {
    private final CosmeticInfo cosmeticInfo; // Linked cosmetic
    private final Map<Class<? extends CosmeticProperty.PlayerData>, CosmeticProperty.PlayerData> typeData;


    public PlayerCosmeticData(CosmeticInfo cosmetic) {
        this.cosmeticInfo = cosmetic;
        typeData = new HashMap<>();
        for (CosmeticProperty<?> type : cosmetic.getProperties().values()) {
            Class<? extends CosmeticProperty.PlayerData> dataClass = type.getPlayerDataClass();
            if (dataClass != null) {
                try {
                    typeData.put(dataClass, type.createPlayerData());
                } catch (Exception e) {
                    throw new RuntimeException("No se pudo instanciar datos de jugador para " + type, e);
                }
            }
        }
    }

    public CosmeticInfo getCosmetic() {
        return cosmeticInfo;
    }

    public <T extends CosmeticProperty.PlayerData> Optional<T> getTypeData(Class<T> typeClass) {
        return Optional.ofNullable(typeClass.cast(typeData.get(typeClass)));
    }
}
