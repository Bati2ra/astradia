package com.astradia.impl;

import com.astradia.api.CosmeticProperty;

import java.util.List;
import java.util.Set;

public class LayeredTextureProperty extends CosmeticProperty<CosmeticProperty.PlayerData> {
    private final List<Layer> layers;

    public LayeredTextureProperty(List<Layer> layers) {
        this.layers = layers;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    @Override
    public Class<PlayerData> getPlayerDataClass() {
        return CosmeticProperty.PlayerData.class;
    }

    @Override
    public Class<? extends CosmeticProperty<PlayerData>> getKey() {
        return LayeredTextureProperty.class;
    }

    @Override
    public List<Set<Class<? extends CosmeticProperty<?>>>> requiredProperties() {
        return List.of(
                Set.of(ModelProperty.class)
        );
    }

    @Override
    public Set<Class<? extends CosmeticProperty<?>>> incompatibleTypes() {
        return Set.of(TextureProperty.class);
    }

    public static class Layer {
        private final String rawPath;
        private final Set<String> targets;
        private final String renderLayer;

        public Layer(String rawPath, Set<String> targets, String renderLayer) {
            this.rawPath = rawPath;
            this.targets = targets;
            this.renderLayer = renderLayer;
        }

        public String getRawPath() {
            return rawPath;
        }

        public Set<String> getTargets() {
            return targets;
        }

        public String getRenderLayer() {
            return renderLayer;
        }
    }
}
