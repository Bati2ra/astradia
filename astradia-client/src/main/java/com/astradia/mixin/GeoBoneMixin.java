package com.astradia.mixin;

import com.astradia.renderer.gecko.GeoBoneAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.cache.model.GeoBone;

@Mixin(GeoBone.class)
public class GeoBoneMixin implements GeoBoneAccessor {
    @Unique
    private boolean shouldCaptureVisualMatrix = false;

    @Unique
    public boolean shouldCaptureVisualMatrix() {
        return this.shouldCaptureVisualMatrix;
    }

    @Unique
    public void setShouldCaptureVisualMatrix(boolean value) {
        this.shouldCaptureVisualMatrix = value;
    }


}
