package com.astradia;

import com.astradia.api.CosmeticPropertyRegistry;
import com.astradia.impl.*;
import net.fabricmc.api.ModInitializer;

public class AstradiaTest implements ModInitializer  {
    @Override
    public void onInitialize() {
        CosmeticPropertyRegistry.register("model", ModelProperty.class);
        CosmeticPropertyRegistry.register("animatedModel", AnimatableType.class);
        CosmeticPropertyRegistry.register("texture", TextureProperty.class);
        CosmeticPropertyRegistry.register("animatedTexture", AnimatedTextureProperty.class);
        CosmeticPropertyRegistry.register("layeredTexture", LayeredTextureProperty.class);
    }
}
