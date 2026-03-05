package com.astradia.impl;

import com.astradia.api.CosmeticProperty;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Set;

public class ModelProperty extends CosmeticProperty<CosmeticProperty.PlayerData> {
    private final Identifier path;

    public ModelProperty(Identifier path) {
        this.path = path;
    }

    public Identifier getPath() {
        return path;
    }

    @Override
    public Class<PlayerData> getPlayerDataClass() {
        return CosmeticProperty.PlayerData.class;
    }

    @Override
    public Class<? extends CosmeticProperty<CosmeticProperty.PlayerData>> getKey() {
        return ModelProperty.class;
    }

    @Override
    public List<Set<Class<? extends CosmeticProperty<?>>>> requiredProperties() {
        return List.of(
                Set.of(TextureProperty.class)
        );
    }
}
