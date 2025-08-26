package com.astradia.impl;

import com.astradia.api.CosmeticProperty;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;

public class TextureProperty extends CosmeticProperty<CosmeticProperty.PlayerData> {
    private final Identifier path;

    public TextureProperty(Identifier path) {
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
    public Class<? extends CosmeticProperty<PlayerData>> getKey() {
        return TextureProperty.class;
    }

    @Override
    public List<Set<Class<? extends CosmeticProperty<?>>>> requiredProperties() {
        return List.of(
                Set.of(ModelProperty.class)
        );
    }
}
