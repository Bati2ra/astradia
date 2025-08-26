package com.astradia.render;

import com.astradia.impl.AnimatableType;
import com.astradia.impl.ModelProperty;
import com.astradia.impl.TextureProperty;
import com.astradia.pojo.ClientCosmeticInfo;
import com.astradia.pojo.CosmeticAnimatable;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Iterator;

public class CosmeticRenderer extends GeoObjectRenderer<CosmeticAnimatable> {
    private final ClientCosmeticInfo cosmetic;
    private final CosmeticAnimatable animatable;

    public CosmeticRenderer(ClientCosmeticInfo cosmetic, CosmeticAnimatable animatable) {
        super(new Model(cosmetic));
        this.cosmetic = cosmetic;
        this.animatable = animatable;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public GeoModel<CosmeticAnimatable> getGeoModel() {
        return model;
    }

    @Override
    public CosmeticAnimatable getAnimatable() {
        return animatable;
    }

    @Override
    public void fireCompileRenderLayersEvent() {

    }

    @Override
    public boolean firePreRenderEvent(MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, float partialTick, int packedLight) {
        return false;
    }

    @Override
    public void firePostRenderEvent(MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, float partialTick, int packedLight) {

    }

    @Override
    public @Nullable RenderLayer getRenderType(CosmeticAnimatable animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return AutoGlowingTexture.getRenderType(texture);
    }

    public void actuallyRenderCosmetic(PlayerEntityRenderer playerEntityRenderer, MatrixStack poseStack, CosmeticAnimatable animatable, BakedGeoModel model, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        poseStack.push();
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        if (!isReRender) {
            long instanceId = this.getInstanceId(animatable);
            this.getGeoModel().handleAnimations(animatable, instanceId, this.createAnimationState(animatable, instanceId, 0.0F, 0.0F, partialTick, false), partialTick);
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());
        if (buffer != null) {

            RenderSystem.setShaderTexture(0, this.getTextureLocation(animatable));
            Iterator var12 = model.topLevelBones().iterator();

            while(var12.hasNext()) {
                GeoBone group = (GeoBone)var12.next();
                copyRotationsDynamically(group, playerEntityRenderer.getModel());
                this.renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
            }}

        poseStack.pop();

    }
    private void copyRotationsDynamically(GeoBone bone, PlayerEntityModel model) {
        if(bone.getName().contentEquals("bipedHead")) {
            RenderUtil.matchModelPartRot(model.head, bone);
            bone.updatePosition(model.head.pivotX, -model.head.pivotY, model.head.pivotZ);
        }
        if(bone.getName().contentEquals("bipedBody")) {
            RenderUtil.matchModelPartRot(model.body, bone);
            bone.updatePosition(model.body.pivotX, -model.body.pivotY, model.body.pivotZ);
        }
        if(bone.getName().contentEquals("bipedRightArm")) {
            RenderUtil.matchModelPartRot(model.rightArm, bone);
            bone.updatePosition(model.rightArm.pivotX + 5.0F, 2.0F - model.rightArm.pivotY, model.rightArm.pivotZ);
        }
        if(bone.getName().contentEquals("bipedLeftArm")) {
            RenderUtil.matchModelPartRot(model.leftArm, bone);
            bone.updatePosition(model.leftArm.pivotX - 5.0F, 2.0F - model.leftArm.pivotY, model.leftArm.pivotZ);
        }
        if(bone.getName().contentEquals("bipedRightLeg")) {
            RenderUtil.matchModelPartRot(model.rightLeg, bone);
            bone.updatePosition(model.rightLeg.pivotX + 2.0F, 12.0F - model.rightLeg.pivotY, model.rightLeg.pivotZ);
        }
        if(bone.getName().contentEquals("bipedLeftLeg")) {
            RenderUtil.matchModelPartRot(model.leftLeg, bone);
            bone.updatePosition(model.leftLeg.pivotX - 2.0F, 12.0F - model.leftLeg.pivotY, model.leftLeg.pivotZ);
        }
    }

    static class Model extends GeoModel<CosmeticAnimatable> {
        ClientCosmeticInfo cosmetic;
        public Model(ClientCosmeticInfo cosmetic){
            this.cosmetic = cosmetic;
        }


        @Override
        public Identifier getModelResource(CosmeticAnimatable cosmeticAnimatable, @Nullable GeoRenderer<CosmeticAnimatable> geoRenderer) {
            var property = cosmetic.getProperty(ModelProperty.class);
            return property.map(ModelProperty::getPath).orElse(null);
        }

        @Override
        public Identifier getTextureResource(CosmeticAnimatable cosmeticAnimatable, @Nullable GeoRenderer<CosmeticAnimatable> geoRenderer) {
            var property = cosmetic.getProperty(TextureProperty.class);
            return property.map(TextureProperty::getPath).orElse(null);
        }

        @Override
        public Identifier getAnimationResource(CosmeticAnimatable cosmeticAnimatable) {
            var property = cosmetic.getProperty(AnimatableType.class);
            return property.map(AnimatableType::getAnimationPath).orElse(null);
        }
    }
}
