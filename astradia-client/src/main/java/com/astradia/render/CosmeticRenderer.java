package com.astradia.render;

import com.astradia.api.player.PlayerCosmeticData;
import com.astradia.impl.AnimatableProperty;
import com.astradia.impl.ColorableProperty;
import com.astradia.impl.ModelProperty;
import com.astradia.impl.TextureProperty;
import com.astradia.pojo.ClientCosmeticInfo;
import com.astradia.pojo.CosmeticAnimatable;
import com.astradia.render.player.PlayerRenderer;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.util.RenderUtil;

public class CosmeticRenderer extends GeoObjectRenderer<CosmeticAnimatable> {
    private final ClientCosmeticInfo cosmetic;
    private final CosmeticAnimatable animatable;
    public PlayerRenderer renderReference;
    public CosmeticRenderer(ClientCosmeticInfo cosmetic, CosmeticAnimatable animatable) {
        super(new Model(cosmetic));
        this.cosmetic = cosmetic;
        this.animatable = animatable;
       // addRenderLayer(new GlowingGeoLayer<>(this));
    }

    @Override
    public void addRenderData(CosmeticAnimatable animatable, Void relatedObject, GeoRenderState renderState) {

    }

    @Override
    public GeoModel<CosmeticAnimatable> getGeoModel() {
        return model;
    }

    public CosmeticAnimatable getAnimatable() {
        return animatable;
    }

    @Override
    public void fireCompileRenderLayersEvent() {

    }

    public void render(MatrixStack poseStack, PlayerCosmeticData cosmeticData, CosmeticAnimatable animatable, @Nullable VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer, int packedLight, float partialTick) {
        if (bufferSource == null) {
            bufferSource = MinecraftClient.getInstance().worldRenderer.bufferBuilders.getEntityVertexConsumers();
        }

        GeoRenderState renderState = this.fillRenderState(animatable, null, new GeoRenderState.Impl(), partialTick);
        renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);
        cosmeticData.getTypeData(ColorableProperty.class, ColorableProperty.PlayerData.class).ifPresent(data -> renderState.addGeckolibData(DataTickets.RENDER_COLOR, data.getColor()));
        this.defaultRender(cosmeticData, renderState, poseStack, (VertexConsumerProvider)bufferSource, renderType, buffer);
    }

    public void defaultRender(PlayerCosmeticData cosmeticData, GeoRenderState renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer) {
        poseStack.push();
        if (renderType == null) {
            renderType = this.getRenderType(renderState, this.getTextureLocation(renderState));
        }

        if (buffer == null && renderType != null) {
            buffer = bufferSource.getBuffer(renderType);
        }

        GeoModel<CosmeticAnimatable> geoModel = this.getGeoModel();
        BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(renderState));
        int packedOverlay = (Integer)renderState.getGeckolibData(DataTickets.PACKED_OVERLAY);
        int packedLight = (Integer)renderState.getGeckolibData(DataTickets.PACKED_LIGHT);
        int renderColor = (Integer)renderState.getGeckolibData(DataTickets.RENDER_COLOR);
        this.preRender(renderState, poseStack, model, bufferSource, buffer, false, packedLight, packedOverlay, renderColor);
        this.adjustPositionForRender(renderState, poseStack, model, false);
        this.scaleModelForRender(renderState, 1.0F, 1.0F, poseStack, model, false);
        if (this.firePreRenderEvent(renderState, poseStack, model, bufferSource)) {
            this.preApplyRenderLayers(renderState, poseStack, model, renderType, bufferSource, buffer, packedLight, packedOverlay, renderColor);
            this.actuallyRender(cosmeticData, renderState, poseStack, model, renderType, bufferSource, buffer, false, packedLight, packedOverlay, renderColor);
            this.applyRenderLayers(renderState, poseStack, model, renderType, bufferSource, buffer, packedLight, packedOverlay, renderColor);
            this.postRender(renderState, poseStack, model, bufferSource, buffer, false, packedLight, packedOverlay, renderColor);
            this.firePostRenderEvent(renderState, poseStack, model, bufferSource);
        }

        poseStack.pop();
        this.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);
        this.doPostRenderCleanup();
    }

    public void actuallyRender(PlayerCosmeticData cosmeticData, GeoRenderState renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        poseStack.push();
        if (!isReRender) {
            this.getGeoModel().handleAnimations(this.createAnimationState(renderState));
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());
        if (buffer != null) {
            for (GeoBone group : model.topLevelBones()) {
                GeoBone bodyPart = getBodyPartByName(renderReference, group.getName());
                MatrixStack newMatrixStack = new MatrixStack();
                newMatrixStack.push();
                newMatrixStack.peek().getNormalMatrix().mul(bodyPart.getWorldSpaceNormal());
                newMatrixStack.peek().getPositionMatrix().mul(bodyPart.getWorldSpaceMatrix());
                newMatrixStack.translate(0, -24 * 0.0625f, 0);
                this.renderRecursively(renderState, newMatrixStack, group, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
                newMatrixStack.pop();
            }  }

        poseStack.pop();
    }

    public void actuallyRenderCosmetic(GeoRenderState renderState, PlayerEntityRenderer playerEntityRenderer, PlayerRenderer<?, ?> playerRenderer, MatrixStack poseStack, CosmeticAnimatable animatable, BakedGeoModel model, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        poseStack.push();
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        if (!isReRender) {
            this.getGeoModel().handleAnimations(this.createAnimationState(renderState));
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());
        if (buffer != null) {
            for (GeoBone group : model.topLevelBones()) {
                GeoBone bodyPart = getBodyPartByName(playerRenderer, group.getName());
                MatrixStack newMatrixStack = new MatrixStack();
                newMatrixStack.push();
                newMatrixStack.peek().getNormalMatrix().mul(bodyPart.getWorldSpaceNormal());
                newMatrixStack.peek().getPositionMatrix().mul(bodyPart.getWorldSpaceMatrix());
                newMatrixStack.translate(0, -24 * 0.0625f, 0);
                this.renderRecursively(renderState, newMatrixStack, group, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
                newMatrixStack.pop();
            }
        }
        poseStack.pop();
    }

    public void renderRecursively(GeoRenderState renderState, MatrixStack poseStack, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {

        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.peek().getPositionMatrix());
            bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.objectRenderTranslations));
        }
        poseStack.push();
        RenderUtil.prepMatrixForBone(poseStack, bone);
        if (!isReRender) {
            Pair<MutableObject<MatrixStack.Entry>, PerBoneRender<GeoRenderState>> boneRenderTask = this.getPerBoneTasks(renderState).get(bone);
            if (boneRenderTask != null) {
                boneRenderTask.left().setValue(poseStack.peek().copy());
            }
        }
        this.renderCubesOfBone(renderState, bone, poseStack, buffer, packedLight, packedOverlay, renderColor);
        this.renderChildBones(renderState, bone, poseStack, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
        poseStack.pop();
    }

    public void renderChildBones(GeoRenderState renderState, GeoBone bone, MatrixStack poseStack, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedColor, int renderColor) {
        if (!bone.isHidingChildren()) {
            for (GeoBone childBone : bone.getChildBones()) {
                this.renderRecursively(renderState, poseStack, childBone, renderType, bufferSource, bufferSource.getBuffer(renderType), isReRender, packedLight, packedColor, renderColor);
            }
        }
    }

    private GeoBone getBodyPartByName(PlayerRenderer<?, ?> renderer, String name) {
        return switch (name) {
            case "bipedRightArm" -> renderer.rightArm;
            case "bipedLeftArm" -> renderer.leftArm;
            case "bipedBody" -> renderer.body;
            case "bipedRightLeg" -> renderer.rightLeg;
            case "bipedLeftLeg" -> renderer.leftLeg;
            default -> renderer.head;
        };
    }

    static class Model extends GeoModel<CosmeticAnimatable> {
        ClientCosmeticInfo cosmetic;
        public Model(ClientCosmeticInfo cosmetic){
            this.cosmetic = cosmetic;
        }

        // TODO: colocar assets "invalidos" como indicador de error
        @Override
        public Identifier getModelResource(GeoRenderState geoRenderState) {
            var property = cosmetic.getProperty(ModelProperty.class);
            return property.map(ModelProperty::getPath).orElse(null);
        }

        @Override
        public Identifier getTextureResource(GeoRenderState geoRenderState) {
            var property = cosmetic.getProperty(TextureProperty.class);
            return property.map(TextureProperty::getPath).orElse(null);
        }

        @Override
        public Identifier getAnimationResource(CosmeticAnimatable cosmeticAnimatable) {
            var property = cosmetic.getProperty(AnimatableProperty.class);
            return property.map(AnimatableProperty::getPath).orElse(null);
        }
    }
}
