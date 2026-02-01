package com.astradia.render.layer;

import com.astradia.AstradiaClient;
import com.astradia.api.player.BodyProportionValues;
import com.astradia.player.PlayerBodyProportions;
import com.astradia.api.player.BodyProportionsConfig;
import com.astradia.render.player.PlayerRenderer;
import com.astradia.utils.AstradiaPlayerEntityRenderState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.UUID;

public class GeckoPlayerHeldItemLayer<T extends LivingEntity & GeoAnimatable, O, R extends PlayerEntityRenderState & GeoRenderState>  extends GeoRenderLayer<T, O, R> {

    public GeckoPlayerHeldItemLayer(GeoRenderer<T, O, R> renderer) {
        super(renderer);
    }

    protected void renderItem(R playerEntityRenderState, ItemRenderState itemRenderState, Arm arm, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (!itemRenderState.isEmpty()) {
            Hand hand = arm == playerEntityRenderState.mainArm ? Hand.MAIN_HAND : Hand.OFF_HAND;
            if (playerEntityRenderState.isUsingItem && playerEntityRenderState.activeHand == hand && playerEntityRenderState.handSwingProgress < 1.0E-5F && !playerEntityRenderState.spyglassState.isEmpty()) {
                this.renderSpyglass(playerEntityRenderState, playerEntityRenderState.spyglassState, arm, matrixStack, vertexConsumerProvider, i);
            } else {
                matrixStack.push();

                var playerRenderer = ((PlayerRenderer) renderer);
                var itemBone = arm.equals(Arm.RIGHT) ? playerRenderer.rightHandItem : playerRenderer.leftHandItem;
                UUID uuid = ((AstradiaPlayerEntityRenderState) playerEntityRenderState).getUuid();
                PlayerBodyProportions playerProportions = AstradiaClient.getPlayerManager().getFromUuid(uuid).getProportions();
                BodyProportionValues config = playerProportions.getValues();
                float armWidth = arm.equals(Arm.RIGHT) ? config.getParameterById("rightArm.width") : config.getParameterById("leftArm.width");
                float armHeight = arm.equals(Arm.RIGHT) ? config.getParameterById("rightArm.length") : config.getParameterById("leftArm.length");
                float width = config.getParameterById("global.width");
                float height = config.getParameterById("global.height");
                var scaleXZ = armWidth * width;
                var scaleY = armHeight * height;
                Vector3f inverseScale = new Vector3f(1.0f / scaleXZ,
                        1.0f / scaleY,
                        1.0f / scaleXZ);
                Matrix4f scaledMatrix = itemBone.getWorldSpaceMatrix();
                Matrix4f unscaledMatrix = new Matrix4f(scaledMatrix);
                unscaledMatrix.scale(inverseScale); // elimina la escala acumulada

                MatrixStack newMatrixStack = new MatrixStack();
                newMatrixStack.peek().getNormalMatrix().mul(itemBone.getWorldSpaceNormal());
                newMatrixStack.peek().getPositionMatrix().mul(unscaledMatrix);
                newMatrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                itemRenderState.render(newMatrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
                matrixStack.pop();
            }
        }
    }

    private void renderSpyglass(R playerEntityRenderState, ItemRenderState spyglassState, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        var playerRenderer = ((PlayerRenderer) renderer);

        var itemBone = arm.equals(Arm.RIGHT) ? playerRenderer.rightHandItem : playerRenderer.leftHandItem;
        UUID uuid = ((AstradiaPlayerEntityRenderState) playerEntityRenderState).getUuid();
        PlayerBodyProportions playerProportions = AstradiaClient.getPlayerManager().getFromUuid(uuid).getProportions();
        BodyProportionValues config = playerProportions.getValues();
        float armWidth = arm.equals(Arm.RIGHT) ? config.getParameterById("rightArm.width") : config.getParameterById("leftArm.width");
        float armHeight = arm.equals(Arm.RIGHT) ? config.getParameterById("rightArm.height") : config.getParameterById("leftArm.height");

        Vector3f inverseScale = new Vector3f(1.0f / armWidth,
                1.0f / armHeight,
                1.0f / armWidth);

        Matrix4f scaledMatrix = itemBone.getWorldSpaceMatrix();
        Matrix4f unscaledMatrix = new Matrix4f(scaledMatrix);
        unscaledMatrix.scale(inverseScale); // elimina la escala acumulada

        MatrixStack newMatrixStack = new MatrixStack();
        newMatrixStack.push();
        newMatrixStack.peek().getNormalMatrix().mul(itemBone.getWorldSpaceNormal());
        newMatrixStack.peek().getPositionMatrix().mul(unscaledMatrix);
        newMatrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
        spyglassState.render(newMatrixStack, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        newMatrixStack.pop();
    }

    @Override
    public void render(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {

        this.renderItem(renderState, renderState.rightHandItemState, Arm.RIGHT, poseStack, bufferSource, packedLight);
        this.renderItem(renderState, renderState.leftHandItemState, Arm.LEFT, poseStack, bufferSource, packedLight);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, R armedEntityRenderState, float f, float g) {
        this.renderItem(armedEntityRenderState, armedEntityRenderState.rightHandItemState, Arm.RIGHT, matrixStack, vertexConsumerProvider, i);
        this.renderItem(armedEntityRenderState, armedEntityRenderState.leftHandItemState, Arm.LEFT, matrixStack, vertexConsumerProvider, i);
    }
}
