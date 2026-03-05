package com.astradia.mixin;

import com.astradia.renderer.gecko.ConfigurablePerBoneRender;
import com.astradia.renderer.gecko.RenderPassInfoAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import org.spongepowered.asm.mixin.*;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.List;
import java.util.Map;

@Mixin(RenderPassInfo.class)
public class RenderPassInfoMixin<R extends GeoRenderState> implements RenderPassInfoAccessor<R> {
    @Unique
    protected final Map<GeoBone, List<ConfigurablePerBoneRender<R>>> vento$boneCustomRenderTasks = new Reference2ObjectArrayMap<>();

    @Override
    @Unique
    public void addPerBoneCustomRender(GeoBone bone, ConfigurablePerBoneRender<R> render) {
        this.vento$boneCustomRenderTasks.computeIfAbsent(bone, key -> new ObjectArrayList<>()).add(render);
    }

    @Override
    @Unique
    public Map<GeoBone, List<ConfigurablePerBoneRender<R>>> getBoneCustomRenderTasks() {
        return this.vento$boneCustomRenderTasks;
    }

}
