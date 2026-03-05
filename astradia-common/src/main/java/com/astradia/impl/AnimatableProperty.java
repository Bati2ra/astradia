package com.astradia.impl;

import com.astradia.api.CosmeticProperty;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Set;

public class AnimatableProperty extends CosmeticProperty<CosmeticProperty.PlayerData> {
    private final Identifier path;

    public AnimatableProperty(Identifier animationPath) {
        this.path = animationPath;
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
        return AnimatableProperty.class;
    }

    @Override
    public List<Set<Class<? extends CosmeticProperty<?>>>> requiredProperties() {
        return List.of(
                Set.of(ModelProperty.class)
        );
    }
}
