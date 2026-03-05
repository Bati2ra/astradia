package com.astradia.renderer.layer;

import com.astradia.renderer.gecko.ConfigurablePerBoneRender;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.function.BiConsumer;

public interface VentoRenderLayer<T extends GeoAnimatable, O, R extends GeoRenderState> {
    default void addPerBoneCustomRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, ConfigurablePerBoneRender<R>> consumer) {}
}
