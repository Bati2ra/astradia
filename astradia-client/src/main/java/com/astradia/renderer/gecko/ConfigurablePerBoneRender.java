package com.astradia.renderer.gecko;

import com.astradia.renderer.util.VentoRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.function.BiFunction;

public class ConfigurablePerBoneRender<R extends GeoRenderState> {
    private final PerBoneCustomRender<R> renderTask;
    private final BoneTransformMode transformMode;

    public ConfigurablePerBoneRender(PerBoneCustomRender<R> renderTask) {
        this(renderTask, BoneTransformMode.DEFAULT);
    }

    public ConfigurablePerBoneRender(PerBoneCustomRender<R> renderTask, BoneTransformMode transformMode) {
        this.renderTask = renderTask;
        this.transformMode = transformMode;
    }

    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, GeoBone bone, SubmitNodeCollector renderTasks) {
        this.renderTask.submitRenderTask(renderPassInfo, bone, renderTasks);
    }

    public boolean applyTransform(PoseStack poseStack, GeoBone bone) {
        return this.transformMode.apply(poseStack, bone);
    }

    public enum BoneTransformMode {
        DEFAULT((poseStack, bone) -> false),
        NO_SCALE((poseStack, bone) -> {
            VentoRenderUtil.transformToBoneWithoutScale(poseStack, bone);
            return true;
        }),
        FIXED((poseStack, bone) -> {
           VentoRenderUtil.transformToBoneFixed(poseStack, bone);
           return true;
        }),
        CUSTOM((poseStack, bone) -> false); // El usuario define su propia lógica

        private final BiFunction<PoseStack, GeoBone, Boolean> transformer;

        BoneTransformMode(BiFunction<PoseStack, GeoBone, Boolean> transformer) {
            this.transformer = transformer;
        }

        public boolean apply(PoseStack poseStack, GeoBone bone) {
            return transformer.apply(poseStack, bone);
        }
    }
}
