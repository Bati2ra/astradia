package com.astradia.renderer.gecko;

import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.List;
import java.util.Map;

public interface RenderPassInfoAccessor<R extends GeoRenderState> {
    void addPerBoneCustomRender(GeoBone bone, ConfigurablePerBoneRender<R> render);
    Map<GeoBone, List<ConfigurablePerBoneRender<R>>> getBoneCustomRenderTasks();

}
