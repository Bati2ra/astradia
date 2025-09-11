package com.astradia.render.layer;

import com.astradia.render.player.PlayerRenderer;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.Function;

public class GeckoHeadLayer<T extends LivingEntity & GeoAnimatable, O, R extends PlayerEntityRenderState & GeoRenderState>  extends GeoRenderLayer<T, O, R> {
    private final HeadFeatureRenderer.HeadTransformation headTransformation;
    private final Function<SkullBlock.SkullType, SkullBlockEntityModel> headModels;

    public GeckoHeadLayer(GeoRenderer<T, O, R> renderer, LoadedEntityModels models) {
        this(renderer, models, HeadFeatureRenderer.HeadTransformation.DEFAULT);
    }

    public GeckoHeadLayer(GeoRenderer<T, O, R> renderer, LoadedEntityModels models, HeadFeatureRenderer.HeadTransformation headTransformation) {
        super(renderer);
        this.headTransformation = headTransformation;
        this.headModels = Util.memoize((type) -> SkullBlockEntityRenderer.getModels(models, type));
    }
    @Override
    public void render(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {

        if (!renderState.headItemRenderState.isEmpty() || renderState.wearingSkullType != null) {
            MatrixStack newMatrixStack = new MatrixStack();
            newMatrixStack.push();
            newMatrixStack.scale(this.headTransformation.horizontalScale(), 1.0F, this.headTransformation.horizontalScale());
            GeoBone bone = ((PlayerRenderer) renderer).head;
            newMatrixStack.peek().getNormalMatrix().mul(bone.getWorldSpaceNormal());
            newMatrixStack.peek().getPositionMatrix().mul(bone.getWorldSpaceMatrix());
            newMatrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            newMatrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            if (renderState.wearingSkullType != null) {
                newMatrixStack.translate(0.0F, this.headTransformation.skullYOffset(), 0.0F);
                newMatrixStack.scale(1.1875F, -1.1875F, -1.1875F);
                newMatrixStack.translate(-0.5, 0.0, -0.5);
                SkullBlock.SkullType skullType = renderState.wearingSkullType;
                SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)this.headModels.apply(skullType);
                RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(skullType, renderState.wearingSkullProfile);
                SkullBlockEntityRenderer.renderSkull((Direction)null, 180.0F, renderState.headItemAnimationProgress, newMatrixStack, bufferSource, packedLight, skullBlockEntityModel, renderLayer);
            } else {
                translate(newMatrixStack, this.headTransformation);
                renderState.headItemRenderState.render(newMatrixStack, bufferSource, packedLight, OverlayTexture.DEFAULT_UV);
            }

            newMatrixStack.pop();
        }
    }

    public static void translate(MatrixStack matrices, HeadFeatureRenderer.HeadTransformation transformation) {
        matrices.translate(0.0F, -0.25F + transformation.yOffset(), 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrices.scale(0.625F, -0.625F, -0.625F);
    }
}
