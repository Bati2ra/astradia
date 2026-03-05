package com.astradia.impl;

import com.astradia.api.CosmeticProperty;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnimatedTextureProperty extends CosmeticProperty<CosmeticProperty.PlayerData> {
    private final Map<Identifier, AnimationConfig> animations;

    public AnimatedTextureProperty(Map<Identifier, AnimationConfig> animations) {
        this.animations = animations;
    }

    public Map<Identifier, AnimationConfig> getAnimations() {
        return animations;
    }

    @Override
    public Class<PlayerData> getPlayerDataClass() {
        return CosmeticProperty.PlayerData.class;
    }

    @Override
    public Class<? extends CosmeticProperty<PlayerData>> getKey() {
        return AnimatedTextureProperty.class;
    }

    @Override
    public List<Set<Class<? extends CosmeticProperty<?>>>> requiredProperties() {
        return List.of(
                Set.of(TextureProperty.class)
        );
    }

    public record AnimationConfig(int frameWidth, int frameHeight, int frameCount, int speed) {
    }
}
