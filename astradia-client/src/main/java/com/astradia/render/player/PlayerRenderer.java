package com.astradia.render.player;

import com.astradia.AstradiaClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Iterator;

public class PlayerRenderer<T extends AbstractClientPlayerEntity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public GeoBone head;
    public GeoBone body;
    public GeoBone rightArm;
    public GeoBone leftArm;
    public GeoBone rightLeg;
    public GeoBone leftLeg;

    public GeoBone root;

    private GeoBone rightArmPh, leftArmPh;

    private GeoBone rSlim, lSlim;
    private GeoBone rWide, lWide;
    private boolean thin;

    private final PlayerEntityRenderer original;

    public PlayerRenderer(EntityRendererFactory.Context context, PlayerModel<T> model, boolean thinArms,
                          PlayerEntityRenderer original) {
        super(context, model);
        this.original = original;
        thin = thinArms;
    }

    private void setupBones() {
        if(root != null) return;
        AstradiaClient.LOGGER.info("[PlayerRenderer] Retrieving bones from player model");
        root = getBoneOrThrow("Root");
        head = getBoneOrThrow("Head");
        body = getBoneOrThrow("Body");
        rightArm = getBoneOrThrow("RightArm");
        leftArm = getBoneOrThrow("LeftArm");
        rightLeg = getBoneOrThrow("RightLeg");
        leftLeg = getBoneOrThrow("LeftLeg");

        rightArmPh = getBoneOrThrow("RightArm_ph");
        leftArmPh = getBoneOrThrow("LeftArm_ph");

        rSlim = getBoneOrThrow("rslim");
        lSlim = getBoneOrThrow("lslim");
        rWide = getBoneOrThrow("rwide");
        lWide = getBoneOrThrow("lwide");

        rWide.setHidden(thin);
        lWide.setHidden(thin);
        rSlim.setHidden(!thin);
        lSlim.setHidden(!thin);
    }

    @Override
    protected R createBaseRenderState(T entity) {
        return (R) new PlayerEntityRenderState();
    }

    @ApiStatus.OverrideOnly
    public void updateRenderState(T entity, R entityRenderState, float partialTick) {
        original.updateRenderState(entity, (PlayerEntityRenderState) entityRenderState, partialTick);
        this.fillRenderState(entity, null, entityRenderState, partialTick);
    }

    @Override
    public void preRender(R renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        setupBones();
        super.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void actuallyRender(R renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        if (!isReRender) {
            float var10003;
            if (renderState instanceof LivingEntityRenderState) {
                LivingEntityRenderState state = (LivingEntityRenderState)renderState;
                var10003 = state.baseScale;
            } else {
                var10003 = 1.0F;
            }

            this.applyRotations(renderState, poseStack, var10003);
            this.getGeoModel().handleAnimations(this.createAnimationState(renderState));
            original.getModel().setAngles((PlayerEntityRenderState) renderState);

            poseStack.translate(0.0F, 0.01F, 0.0F);
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());
        copyOriginalRotations();
        applyBodyProportions(renderState);
        if (buffer != null) {
            if (renderType != null) {
                Iterator<GeoBone> var11 = model.topLevelBones().iterator();

                while(var11.hasNext()) {
                    GeoBone group = var11.next();
                    this.renderRecursively(renderState, poseStack, group, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
                }

            }
        }

    }

    @Override
    public void fireCompileRenderLayersEvent() {

    }

    @Override
    public void fireCompileRenderStateEvent(T t, Void o, R playerRenderState) {

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

    private void copyVisibilityFrom(ModelPart from, GeoBone to) {
        to.setHidden(!from.visible);
    }

    private void copyOriginalRotations() {
        float headXYZ = 1f;
        float bodyXZ = 1.3f, bodyY = 0.6f;
        float rightArmXZ = 1f, rightArmY = 0.6f;
        float leftArmXZ = 1F, leftArmY = 0.6f;
        float rightLegXZ = 1f, rightLegY = 0.5f;
        float leftLegXZ = 1, leftLegY = 0.5f;

        var bodyVerticalOffset = (bodyY - 1.0f) * 12;
        float legDeltaY  = (Math.max(rightLegY, leftLegY)  - 1.0f) * 12;
        root.setPosY(bodyVerticalOffset + legDeltaY);

        head.updateScale(headXYZ, headXYZ, headXYZ);
        body.updateScale(bodyXZ, bodyY, bodyXZ);
        rightArm.updateScale(rightArmXZ, rightArmY, rightArmXZ);
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

    private void applyBodyProportions(R renderState) {

    }

    @Override
    protected void applyRotations(R renderState, MatrixStack poseStack, float nativeScale) {
        PlayerEntityRenderState playerEntityRenderState = (PlayerEntityRenderState) renderState;
        float h = playerEntityRenderState.leaningPitch;
        float i = playerEntityRenderState.pitch;
        float j;
        if (playerEntityRenderState.isGliding) {
            super.applyRotations(renderState, poseStack, nativeScale);
            j = playerEntityRenderState.getGlidingProgress();
            if (!playerEntityRenderState.usingRiptide) {
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * (-90.0F - i)));
            }

            if (playerEntityRenderState.applyFlyingRotation) {
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotation(playerEntityRenderState.flyingRotation));
            }
        } else if (h > 0.0F) {
            super.applyRotations(renderState, poseStack, nativeScale);
            j = playerEntityRenderState.touchingWater ? -90.0F - i : -90.0F;
            float k = MathHelper.lerp(h, 0.0F, j);
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(k));
            if (playerEntityRenderState.isSwimming) {
                poseStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.applyRotations(renderState, poseStack, nativeScale);
        }
    }
}
