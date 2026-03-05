package com.astradia.renderer.layer.builtIn;

import com.astradia.renderer.gecko.ConfigurablePerBoneRender;
import com.astradia.renderer.layer.VentoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class VentoHeadLayer<T extends LivingEntity & GeoAnimatable, O, R extends LivingEntityRenderState> extends GeoRenderLayer<T, O, R> implements VentoRenderLayer<T, O, R> {
    private static final float ITEM_SCALE = 0.625F;
    private static final float SKULL_SCALE = 1.1875F;
    private final CustomHeadLayer.Transforms transforms;
    private final Function<SkullBlock.Type, SkullModelBase> skullModels;
    private final PlayerSkinRenderCache playerSkinRenderCache;

    public VentoHeadLayer(GeoRenderer<T, O, R> renderer, EntityModelSet entityModelSet, PlayerSkinRenderCache playerSkinRenderCache) {
        this(renderer, entityModelSet, playerSkinRenderCache, CustomHeadLayer.Transforms.DEFAULT);
    }

    public VentoHeadLayer(GeoRenderer<T, O, R> renderer, EntityModelSet entityModelSet, PlayerSkinRenderCache playerSkinRenderCache, CustomHeadLayer.Transforms transforms) {
        super(renderer);
        this.transforms = transforms;
        this.skullModels = Util.memoize((type) -> SkullBlockRenderer.createModel(entityModelSet, type));
        this.playerSkinRenderCache = playerSkinRenderCache;
    }

    @Override
    public void addPerBoneCustomRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, ConfigurablePerBoneRender<R>> consumer) {
        renderPassInfo.model().getBone("Head").ifPresent(bone -> render(bone, consumer));
    }

    private void render(GeoBone bone, BiConsumer<GeoBone, ConfigurablePerBoneRender<R>> consumer) {
        consumer.accept(bone, new ConfigurablePerBoneRender<>((renderPassInfo, bone1, renderTasks) -> {
            final R bipedEntityRenderState = renderPassInfo.renderState();
            final PoseStack matrixStack = renderPassInfo.poseStack();
            submit(matrixStack, renderTasks, renderPassInfo.packedLight(), bipedEntityRenderState, -1, -1);

        }, ConfigurablePerBoneRender.BoneTransformMode.FIXED));
    }

    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, R livingEntityRenderState, float f, float g) {
        if (!livingEntityRenderState.headItem.isEmpty() || livingEntityRenderState.wornHeadType != null) {
            poseStack.pushPose();
            poseStack.scale(this.transforms.horizontalScale(), 1.0F, this.transforms.horizontalScale());
            if (livingEntityRenderState.wornHeadType != null) {
                poseStack.translate(0.0F, this.transforms.skullYOffset(), 0.0F);
                poseStack.scale(-1, -1, 1);
                poseStack.translate(0, -1.501F, 0);
                poseStack.scale(1.1875F, -1.1875F, -1.1875F);
                poseStack.translate(-0.5, 0, -0.5);
                SkullBlock.Type type = livingEntityRenderState.wornHeadType;
                SkullModelBase skullModelBase = (SkullModelBase)this.skullModels.apply(type);
                RenderType renderType = this.resolveSkullRenderType(livingEntityRenderState, type);
                SkullBlockRenderer.submitSkull((Direction)null, 180.0F, livingEntityRenderState.wornHeadAnimationPos, poseStack, submitNodeCollector, i, skullModelBase, renderType, livingEntityRenderState.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
            } else {
                livingEntityRenderState.headItem.submit(poseStack, submitNodeCollector, i, OverlayTexture.NO_OVERLAY, livingEntityRenderState.outlineColor);
            }

            poseStack.popPose();
        }
    }

    private RenderType resolveSkullRenderType(LivingEntityRenderState livingEntityRenderState, SkullBlock.Type type) {
        if (type == SkullBlock.Types.PLAYER) {
            ResolvableProfile resolvableProfile = livingEntityRenderState.wornHeadProfile;
            if (resolvableProfile != null) {
                return this.playerSkinRenderCache.getOrDefault(resolvableProfile).renderType();
            }
        }

        return SkullBlockRenderer.getSkullRenderType(type, (Identifier)null);
    }
}
