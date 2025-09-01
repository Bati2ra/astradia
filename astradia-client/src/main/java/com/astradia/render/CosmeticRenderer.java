package com.astradia.render;

import com.astradia.impl.AnimatableType;
import com.astradia.impl.ModelProperty;
import com.astradia.impl.TextureProperty;
import com.astradia.pojo.ClientCosmeticDefinition;
import com.astradia.pojo.CosmeticAnimatable;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Iterator;

public class CosmeticRenderer extends GeoObjectRenderer<CosmeticAnimatable> {
    private final ClientCosmeticDefinition cosmetic;
    private final CosmeticAnimatable animatable;

    public CosmeticRenderer(ClientCosmeticDefinition cosmetic, CosmeticAnimatable animatable) {
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

    public void actuallyRenderCosmetic(GeoRenderState renderState, PlayerEntityRenderer playerEntityRenderer, MatrixStack poseStack, CosmeticAnimatable animatable, BakedGeoModel model, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        poseStack.push();
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        if (!isReRender) {
            this.getGeoModel().handleAnimations(this.createAnimationState(renderState));
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());
        if (buffer != null) {

            //RenderSystem.setShaderTexture(0, this.getTextureLocation(renderState));
            Iterator var12 = model.topLevelBones().iterator();

            while(var12.hasNext()) {
                GeoBone group = (GeoBone)var12.next();
                copyRotationsDynamically(group, playerEntityRenderer.getModel());
                this.renderRecursively(renderState, poseStack, group, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
            }}

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
            Pair<MutableObject<MatrixStack.Entry>, PerBoneRender<GeoRenderState>> boneRenderTask = (Pair)this.getPerBoneTasks(renderState).get(bone);
            if (boneRenderTask != null) {
                ((MutableObject)boneRenderTask.left()).setValue(poseStack.peek().copy());
            }
        }

        this.renderCubesOfBone(renderState, bone, poseStack, buffer, packedLight, packedOverlay, renderColor);
        this.renderChildBones(renderState, bone, poseStack, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
        poseStack.pop();
    }

    public void renderChildBones(GeoRenderState renderState, GeoBone bone, MatrixStack poseStack, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedColor, int renderColor) {
        if (!bone.isHidingChildren()) {
            Iterator var11 = bone.getChildBones().iterator();

            while(var11.hasNext()) {
                GeoBone childBone = (GeoBone)var11.next();
                this.renderRecursively(renderState, poseStack, childBone, renderType, bufferSource, bufferSource.getBuffer(renderType), isReRender, packedLight, packedColor, renderColor);
            }

        }
    }

    private void copyRotationsDynamically(GeoBone bone, PlayerEntityModel model) {
        if(bone.getName().contentEquals("bipedHead")) {
            RenderUtil.matchModelPartRot(model.head, bone);
            bone.updatePosition(model.head.originX, -model.head.originY, model.head.originZ);
        }
        if(bone.getName().contentEquals("bipedBody")) {
            RenderUtil.matchModelPartRot(model.body, bone);
            bone.updatePosition(model.body.originX, -model.body.originY, model.body.originZ);
        }
        if(bone.getName().contentEquals("bipedRightArm")) {
            RenderUtil.matchModelPartRot(model.rightArm, bone);
            bone.updatePosition(model.rightArm.originX + 5.0F, 2.0F - model.rightArm.originY, model.rightArm.originZ);
        }
        if(bone.getName().contentEquals("bipedLeftArm")) {
            RenderUtil.matchModelPartRot(model.leftArm, bone);
            bone.updatePosition(model.leftArm.originX - 5.0F, 2.0F - model.leftArm.originY, model.leftArm.originZ);
        }
        if(bone.getName().contentEquals("bipedRightLeg")) {
            RenderUtil.matchModelPartRot(model.rightLeg, bone);
            bone.updatePosition(model.rightLeg.originX + 2.0F, 12.0F - model.rightLeg.originY, model.rightLeg.originZ);
        }
        if(bone.getName().contentEquals("bipedLeftLeg")) {
            RenderUtil.matchModelPartRot(model.leftLeg, bone);
            bone.updatePosition(model.leftLeg.originX - 2.0F, 12.0F - model.leftLeg.originY, model.leftLeg.originZ);
        }
    }

    static class Model extends GeoModel<CosmeticAnimatable> {
        ClientCosmeticDefinition cosmetic;
        public Model(ClientCosmeticDefinition cosmetic){
            this.cosmetic = cosmetic;
        }

        @Override
        public Identifier getModelResource(GeoRenderState geoRenderState) {
            var property = cosmetic.getVariant("default").getProperty(ModelProperty.class);
            return property.map(ModelProperty::getPath).orElse(null);
        }

        @Override
        public Identifier getTextureResource(GeoRenderState geoRenderState) {
            var property = cosmetic.getVariant("default").getProperty(TextureProperty.class);
            return property.map(TextureProperty::getPath).orElse(null);
        }

        @Override
        public Identifier getAnimationResource(CosmeticAnimatable cosmeticAnimatable) {
            var property = cosmetic.getVariant("default").getProperty(AnimatableType.class);
            return property.map(AnimatableType::getAnimationPath).orElse(null);
        }
    }
}
