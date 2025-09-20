package com.astradia.render.player;

import com.astradia.AstradiaClient;
import com.astradia.player.PlayerBodyProportions;
import com.astradia.api.player.BodyPartProportion;
import com.astradia.api.player.BodyProportionsConfig;
import com.astradia.render.GeoBoneAccessor;
import com.astradia.render.PatchedArmorEntityModel;
import com.astradia.render.layer.*;
import com.astradia.utils.AstradiaPlayerEntityRenderState;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.util.RenderUtil;

import java.lang.Math;
import java.util.UUID;

public class PlayerRenderer<T extends AbstractClientPlayerEntity & GeoAnimatable, R extends PlayerEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public GeoBone head;
    public GeoBone body;
    public GeoBone rightArm;
    public GeoBone leftArm;
    public GeoBone rightLeg;
    public GeoBone leftLeg;

    public GeoBone rightHandItem;
    public GeoBone leftHandItem;

    public GeoBone root;

    public GeoBone rightArmPh, leftArmPh;

    private GeoBone rightArmSlim, leftArmSlim;
    private GeoBone rightArmClassic, leftArmClassic;
    private GeoBone rightArmLayerSlim, leftArmLayerSlim;
    private GeoBone rightArmLayerClassic, leftArmLayerClassic;

    private boolean thin;
    
    private GeoBone hat;
    private GeoBone jacket;
    private GeoBone rightSleeve;
    private GeoBone leftSleeve;
    private GeoBone rightPants;
    private GeoBone leftPants;

    public final PlayerEntityRenderer original;

    public PlayerRenderer(EntityRendererFactory.Context context, PlayerModel<T> model, boolean thinArms,
                          PlayerEntityRenderer original) {
        super(context, model);
        this.original = original;
        thin = thinArms;

        addRenderLayer(new GeckoArmorLayer<>(this, new PatchedArmorEntityModel<>(this, context.getPart(thinArms ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)), new PatchedArmorEntityModel<>(this, context.getPart(thinArms ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR)), context.getEquipmentRenderer()));
        addRenderLayer(new GeckoPlayerHeldItemLayer<>(this));
        addRenderLayer(new GeckoHeadLayer<>(this, context.getEntityModels()));
        addRenderLayer(new GeckoElytraLayer<>(this, context.getEntityModels(), context.getEquipmentRenderer()));
        addRenderLayer(new GeckoCapeLayer<>(this, context.getEntityModels(), context.getEquipmentModelLoader()));
    }

    private void retrievePlayerBones() {
        if(root != null) return;
        AstradiaClient.LOGGER.info("[PlayerRenderer] Retrieving bones from player model");
        root = getBoneOrThrow("Root");
        head = getBoneOrThrow("Head");
        body = getBoneOrThrow("Body");
        rightArm = getBoneOrThrow("RightArm");
        leftArm = getBoneOrThrow("LeftArm");
        rightLeg = getBoneOrThrow("RightLeg");
        leftLeg = getBoneOrThrow("LeftLeg");
        
        hat = getBoneOrThrow("HeadLayer");
        jacket = getBoneOrThrow("BodyLayer");
        rightSleeve = getBoneOrThrow("RightArmLayer");
        leftSleeve = getBoneOrThrow("LeftArmLayer");
        rightPants = getBoneOrThrow("RightLegLayer");
        leftPants = getBoneOrThrow("LeftLegLayer");
        
        // utility
        rightArmPh = getBoneOrThrow("RightArmScalePoint");
        leftArmPh = getBoneOrThrow("LeftArmScalePoint");
        rightHandItem = getBoneOrThrow("RightHandItem");
        leftHandItem = getBoneOrThrow("LeftHandItem");

        rightArmSlim = getBoneOrThrow("RightArmSlim");
        leftArmSlim = getBoneOrThrow("LeftArmSlim");
        rightArmClassic = getBoneOrThrow("RightArmClassic");
        leftArmClassic = getBoneOrThrow("LeftArmClassic");

        rightArmLayerSlim = getBoneOrThrow("RightArmLayerSlim");
        leftArmLayerSlim = getBoneOrThrow("LeftArmLayerSlim");
        rightArmLayerClassic = getBoneOrThrow("RightArmLayerClassic");
        leftArmLayerClassic = getBoneOrThrow("LeftArmLayerClassic");

        ((GeoBoneAccessor) root).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) head).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) rightArm).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) leftArm).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) rightArmPh).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) leftArmPh).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) rightHandItem).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) leftHandItem).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) body).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) rightLeg).setShouldCaptureVisualMatrix(true);
        ((GeoBoneAccessor) leftLeg).setShouldCaptureVisualMatrix(true);
    }

    @Override
    protected R createBaseRenderState(T entity) {
        return (R) new PlayerEntityRenderState();
    }

    @ApiStatus.OverrideOnly
    public void updateRenderState(T entity, R entityRenderState, float partialTick) {
        original.updateRenderState(entity, entityRenderState, partialTick);
        this.fillRenderState(entity, null, entityRenderState, partialTick);
    }

    @Override
    public void preRender(R renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        retrievePlayerBones();
        super.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void actuallyRender(R renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        if (!isReRender) {
            float var10003;
            if (renderState != null) {
                var10003 = renderState.baseScale;
            } else {
                var10003 = 1.0F;
            }

            this.applyRotations(renderState, poseStack, var10003);
            this.getGeoModel().handleAnimations(this.createAnimationState(renderState));
            original.getModel().setAngles(renderState);

            poseStack.translate(0.0F, 0.01F, 0.0F);
        }
        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());
        this.withScale(0.9375F);
        setupBoneVisibility(renderState);
        copyOriginalRotations(renderState);
        if (buffer != null) {
            if (renderType != null) {
                poseStack.push();
                this.renderRecursively(renderState, poseStack, root, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
                poseStack.pop();

            }
        }
    }

    @Override
    public void renderRecursively(R renderState, MatrixStack poseStack, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        poseStack.push();
        RenderUtil.translateMatrixToBone(poseStack, bone);
        RenderUtil.translateToPivotPoint(poseStack, bone);
        RenderUtil.rotateMatrixAroundBone(poseStack, bone);
        RenderUtil.scaleMatrixForBone(poseStack, bone);

        // Captura visual directa (para ítems, efectos, etc.)
        if(((GeoBoneAccessor) bone).shouldCaptureVisualMatrix()) {
            poseStack.push();
            MatrixStack.Entry entry = poseStack.peek();
            bone.setWorldSpaceNormal(new Matrix3f(entry.getNormalMatrix()));
            bone.setWorldSpaceMatrix(new Matrix4f(entry.getPositionMatrix()));
            poseStack.pop();
        } else {
            // Lógica original de tracking (model/local/world space), no son compatibles entre sí
            if (bone.isTrackingMatrices()) {
                Matrix4f poseState = new Matrix4f(poseStack.peek().getPositionMatrix());
                Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);
                bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
                bone.setLocalSpaceMatrix(RenderUtil.translateMatrix(localMatrix, this.getPositionOffset(renderState).toVector3f()));
                bone.setWorldSpaceMatrix(RenderUtil.translateMatrix(new Matrix4f(localMatrix), new Vector3f((float)renderState.x, (float)renderState.y, (float)renderState.z)));
            }
        }
        RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
        if (!isReRender) {
            Pair<MutableObject<MatrixStack.Entry>, PerBoneRender<R>> boneRenderTask = (Pair)this.getPerBoneTasks(renderState).get(bone);
            if (boneRenderTask != null) {
                boneRenderTask.left().setValue(poseStack.peek().copy());
            }
        }
        this.renderCubesOfBone(renderState, bone, poseStack, buffer, packedLight, packedOverlay, renderColor);
        this.renderChildBones(renderState, bone, poseStack, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
        poseStack.pop();
    }

    @Override
    public void fireCompileRenderLayersEvent() {

    }

    @Override
    public void fireCompileRenderStateEvent(T t, Void o, R playerRenderState) {
        GeckoLibServices.Client.EVENTS.fireCompileEntityRenderLayers(this);
    }

    @Override
    public boolean firePreRenderEvent(R playerRenderState, MatrixStack matrixStack, BakedGeoModel bakedGeoModel, VertexConsumerProvider vertexConsumerProvider) {
        return true;
    }

    @Override
    public void firePostRenderEvent(R playerRenderState, MatrixStack matrixStack, BakedGeoModel bakedGeoModel, VertexConsumerProvider vertexConsumerProvider) {

    }

    private GeoBone getBoneOrThrow(String bone) {
        return model.getBone(bone).orElseThrow(() -> new RuntimeException("[PlayerRenderer] Couldn't find bone '" + bone + "', check if its missing."));
    }
    
    private void setupBoneVisibility(R state) {
        boolean bl = state.spectator;
        body.setHidden(bl);
        rightArm.setHidden(bl);
        leftArm.setHidden(bl);
        rightLeg.setHidden(bl);
        leftLeg.setHidden(bl);

        hat.setHidden(!state.hatVisible);
        jacket.setHidden(!state.jacketVisible);
        rightSleeve.setHidden(!state.rightSleeveVisible);
        leftSleeve.setHidden(!state.leftSleeveVisible);
        rightPants.setHidden(!state.rightPantsLegVisible);
        leftPants.setHidden(!state.leftPantsLegVisible);

        rightArmClassic.setHidden(thin);
        leftArmClassic.setHidden(thin);
        rightArmSlim.setHidden(!thin);
        leftArmSlim.setHidden(!thin);

        rightArmLayerClassic.setHidden(thin);
        leftArmLayerClassic.setHidden(thin);
        rightArmLayerSlim.setHidden(!thin);
        leftArmLayerSlim.setHidden(!thin);
    }

    private void copyOriginalRotations(R state) {
        UUID uuid = ((AstradiaPlayerEntityRenderState) state).getUuid();
        PlayerBodyProportions playerProportions = AstradiaClient.getPlayerManager().getFromUuid(uuid).getProportions();
        BodyProportionsConfig config = playerProportions.getConfig();

        float headXYZ = config.head.getValue(BodyPartProportion.Axis.X);
        float bodyXZ = config.torso.getValue(BodyPartProportion.Axis.X),
                bodyY = config.torso.getValue(BodyPartProportion.Axis.Y);
        float rightArmXZ = config.rightArm.getValue(BodyPartProportion.Axis.X),
                rightArmY = config.rightArm.getValue(BodyPartProportion.Axis.Y);
        float leftArmXZ = config.leftArm.getValue(BodyPartProportion.Axis.X),
                leftArmY = config.leftArm.getValue(BodyPartProportion.Axis.Y);
        float rightLegXZ = config.rightLeg.getValue(BodyPartProportion.Axis.X),
                rightLegY = config.rightLeg.getValue(BodyPartProportion.Axis.Y);
        float leftLegXZ = config.leftLeg.getValue(BodyPartProportion.Axis.X),
                leftLegY = config.leftLeg.getValue(BodyPartProportion.Axis.Y);
        float width = config.width.getValue(BodyPartProportion.Axis.X);
        float height = config.height.getValue(BodyPartProportion.Axis.Y);

        var bodyVerticalOffset = (bodyY - 1.0f) * 12;
        float legDeltaY  = (Math.max(rightLegY, leftLegY)  - 1.0f) * 12;
        root.setPosY(bodyVerticalOffset + legDeltaY);

        root.updateScale(width, height, width);
        head.updateScale(headXYZ, headXYZ, headXYZ);
        body.updateScale(bodyXZ, bodyY, bodyXZ);
        rightArmPh.updateScale(rightArmXZ, rightArmY, rightArmXZ);
        leftArmPh.updateScale(leftArmXZ, leftArmY, leftArmXZ);
        rightLeg.updateScale(rightLegXZ, rightLegY, rightLegXZ);
        leftLeg.updateScale(leftLegXZ, leftLegY, leftLegXZ);

        var originalModel = original.getModel();
        head.updatePosition(originalModel.head.originX, -originalModel.head.originY, originalModel.head.originZ);
        RenderUtil.matchModelPartRot(originalModel.head, this.head);

        body.updatePosition(originalModel.body.originX, -originalModel.body.originY, originalModel.body.originZ);
        RenderUtil.matchModelPartRot(originalModel.body, this.body);

        rightArm.updatePosition(originalModel.rightArm.originX + 5.0F + (bodyXZ - 1.0f) * -4, 2.0F - originalModel.rightArm.originY, originalModel.rightArm.originZ);
        RenderUtil.matchModelPartRot(originalModel.rightArm, this.rightArm);

        RenderUtil.matchModelPartRot(originalModel.leftArm, this.leftArm);
        leftArm.updatePosition(originalModel.leftArm.originX - 5.0F + (bodyXZ - 1.0f) * 4, 2.0F - originalModel.leftArm.originY, originalModel.leftArm.originZ);

        leftLeg.updatePosition(originalModel.leftLeg.originX - 2.0F, 12.0F - originalModel.leftLeg.originY - bodyVerticalOffset, originalModel.leftLeg.originZ);
        RenderUtil.matchModelPartRot(originalModel.leftLeg, this.leftLeg);

        RenderUtil.matchModelPartRot(originalModel.rightLeg, this.rightLeg);
        rightLeg.updatePosition(originalModel.rightLeg.originX + 2.0F, 12.0F - originalModel.rightLeg.originY - bodyVerticalOffset, originalModel.rightLeg.originZ);
    }

    @Override
    protected void applyRotations(R renderState, MatrixStack poseStack, float nativeScale) {
        float h = renderState.leaningPitch;
        float i = renderState.pitch;
        float j;
        if (renderState.isGliding) {
            super.applyRotations(renderState, poseStack, nativeScale);
            j = renderState.getGlidingProgress();
            if (!renderState.usingRiptide) {
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * (-90.0F - i)));
            }

            if (renderState.applyFlyingRotation) {
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotation(renderState.flyingRotation));
            }
        } else if (h > 0.0F) {
            super.applyRotations(renderState, poseStack, nativeScale);
            j = renderState.touchingWater ? -90.0F - i : -90.0F;
            float k = MathHelper.lerp(h, 0.0F, j);
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(k));
            if (renderState.isSwimming) {
                poseStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.applyRotations(renderState, poseStack, nativeScale);
        }
    }
}
