package com.astradia.renderer.custom;

import com.astradia.api.player.PlayerCosmeticData;
import com.astradia.impl.AnimatableProperty;
import com.astradia.impl.ColorableProperty;
import com.astradia.impl.ModelProperty;
import com.astradia.impl.TextureProperty;
import com.astradia.pojo.ClientCosmeticDefinition;
import com.astradia.pojo.CosmeticAnimatable;
import com.astradia.renderer.entity.player.HairPhysics;
import com.astradia.renderer.entity.player.PlayerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class CosmeticRenderer extends GeoObjectRenderer<CosmeticAnimatable, Void, GeoRenderState> {
    private final ClientCosmeticDefinition cosmetic;
    private CosmeticAnimatable animatable;
    private final HairPhysics hairPhysics = new HairPhysics();
    public CosmeticRenderer(ClientCosmeticDefinition cosmetic, CosmeticAnimatable animatable) {
        super(new Model(cosmetic));
        this.cosmetic = cosmetic;
        this.animatable = animatable;
    }

    public CosmeticAnimatable getAnimatable() {
        return animatable;
    }

    @Override
    public void setMolangQueryValues(CosmeticAnimatable animatable, @org.jspecify.annotations.Nullable Void relatedObject, GeoRenderState renderState, float partialTick) {
        /*MathParser.setVariable("query.physics_pitch", actor -> {
            return hairPhysics.getPitchRotation();
        });

        MathParser.setVariable("query.physics_yaw", actor -> {
            return hairPhysics.getYawRotation();
        });*/
    }

    @Override
    public void performRenderPass(GeoRenderState renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        this.performRenderPass(renderState, poseStack, renderTasks, cameraState, null);
    }

    @Override
    public void performRenderPass(GeoRenderState renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState, RenderPassInfo.@org.jspecify.annotations.Nullable BoneUpdater<GeoRenderState> boneUpdater) {
        poseStack.pushPose();
        RenderType renderType = this.getRenderType(renderState, this.getTextureLocation(renderState));
        RenderPassInfo<GeoRenderState> renderPassInfo = RenderPassInfo.create(this, renderState, poseStack, cameraState, renderType != null);
        if (boneUpdater != null) {
            renderPassInfo.addBoneUpdater(boneUpdater);
        }

        if (this.firePreRenderEvent(renderPassInfo, renderTasks)) {
            this.preRenderPass(renderPassInfo, renderTasks);
            this.scaleModelForRender(renderPassInfo, 1.0F, 1.0F);
            this.adjustRenderPose(renderPassInfo);
            this.preApplyRenderLayers(renderPassInfo, renderTasks);
            renderPassInfo.captureModelRenderPose();
            this.submitRenderTasks(renderPassInfo, renderTasks, renderType);
            this.submitPerBoneRenderTasks(renderPassInfo, renderTasks);
            this.applyRenderLayers(renderPassInfo, renderTasks);
        }

        poseStack.popPose();
        this.postRenderPass(renderPassInfo, renderTasks);
    }


    public void performRenderPass(CosmeticAnimatable animatable, AvatarRenderState avatarRenderState, PlayerCosmeticData playerCosmeticData, Void relatedObject, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState, int packedLight, float partialTick, double tick, RenderPassInfo.BoneUpdater<GeoRenderState> boneUpdater) {
        GeoRenderState renderState = this.fillRenderState(animatable, relatedObject, this.createRenderState(animatable, null), partialTick);

        renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);
        playerCosmeticData.getTypeData(ColorableProperty.class, ColorableProperty.PlayerData.class).ifPresent(colorData -> renderState.addGeckolibData(DataTickets.RENDER_COLOR, colorData.getColor()));
        renderState.addGeckolibData(DataTickets.ENTITY_PITCH, avatarRenderState.xRot);
        renderState.addGeckolibData(DataTickets.ENTITY_YAW, avatarRenderState.yRot);
  //      renderState.addGeckolibData(DataTickets.POSITION, avatarRenderState.getGeckolibData(DataTickets.POSITION));
//        hairPhysics.update(avatarRenderState.xRot, avatarRenderState.yRot, avatarRenderState.getOrDefaultGeckolibData(DataTickets.VELOCITY, Vec3.ZERO), partialTick);

        this.performRenderPass(renderState, poseStack, renderTasks, cameraState, boneUpdater);
    }


    public void actuallyRender(PlayerCosmeticData cosmeticData, GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
       /* poseStack.push();
        if (!isReRender) {
            this.getGeoModel().handleAnimations(this.createAnimationState(renderState));
        }
        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());
        if (buffer != null) {
            for (GeoBone group : model.topLevelBones()) {
                GeoBone bodyPart = getBodyPartByName(renderReference, group.name());
                MatrixStack newMatrixStack = new MatrixStack();
                newMatrixStack.push();

                newMatrixStack.peek().getNormalMatrix().mul(bodyPart.getWorldSpaceNormal());
                newMatrixStack.peek().getPositionMatrix().mul(bodyPart.getWorldSpaceMatrix());
                newMatrixStack.translate(0, -24 * 0.0625f, 0);
                this.renderRecursively(renderState, newMatrixStack, group, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
                newMatrixStack.pop();
            }  }

        poseStack.pop();*/
    }

    public void actuallyRenderCosmetic(GeoRenderState renderState, AvatarRenderer playerEntityRenderer, PlayerRenderer<?, ?> playerRenderer, PoseStack poseStack, CosmeticAnimatable animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
      /*  poseStack.push();
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
        poseStack.pop();*/
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> renderPassInfo) {}

    public void renderRecursively(GeoRenderState renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
/*
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
        poseStack.pop();*/
    }

    public void renderChildBones(GeoRenderState renderState, GeoBone bone, PoseStack poseStack, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedColor, int renderColor) {
        /*if (!bone.isHidingChildren()) {
            for (GeoBone childBone : bone.getChildBones()) {
                this.renderRecursively(renderState, poseStack, childBone, renderType, bufferSource, bufferSource.getBuffer(renderType), isReRender, packedLight, packedColor, renderColor);
            }
        }*/
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
        ClientCosmeticDefinition cosmetic;
        public Model(ClientCosmeticDefinition cosmetic){
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
