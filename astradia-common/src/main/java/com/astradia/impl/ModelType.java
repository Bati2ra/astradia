package com.astradia.impl;

import com.astradia.api.CosmeticProperty;
import net.minecraft.util.Identifier;

public class ModelType extends CosmeticProperty<CosmeticProperty.PlayerData> {
    private final Identifier texturePath;
    private final Identifier modelPath;

    public ModelType(Identifier texturePath, Identifier modelPath) {
        super();
        this.texturePath = texturePath;
        this.modelPath = modelPath;
    }

    public Identifier getTexturePath() {
        return texturePath;
    }

    public Identifier getModelPath() {
        return modelPath;
    }

    @Override
    public Class<PlayerData> getPlayerDataClass() {
        return null;
    }

    @Override
    public Class<? extends CosmeticProperty<CosmeticProperty.PlayerData>> getKey() {
        return ModelType.class;
    }
}
