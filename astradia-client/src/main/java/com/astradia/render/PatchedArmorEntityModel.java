package com.astradia.render;

import com.astradia.render.player.PlayerRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class PatchedArmorEntityModel<T extends AbstractClientPlayerEntity & GeoAnimatable, R extends PlayerEntityRenderState & GeoRenderState> extends ArmorEntityModel<R> {
    private final PlayerRenderer<T, R> renderer;

    public PatchedArmorEntityModel(PlayerRenderer<T, R> renderer, ModelPart modelPart) {
        super(modelPart);
        this.renderer = renderer;
    }

    @Override
    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        MatrixStack newMatrixStack = new MatrixStack();
        GeoBone bone = renderer.head;
        moveToMatrix(bone, newMatrixStack, () -> this.head.render(newMatrixStack, vertices, light, overlay, color));

        bone = renderer.rightArmPh;
        moveToMatrix(bone, newMatrixStack, () -> {
            newMatrixStack.translate(4   / 16F,0,0);
            this.rightArm.render(newMatrixStack, vertices, light, overlay, color);
        });

        bone = renderer.leftArmPh;
        moveToMatrix(bone, newMatrixStack, () -> {
            newMatrixStack.translate(-4   / 16F,0,0);
            this.leftArm.render(newMatrixStack, vertices, light, overlay, color);
        });

        bone = renderer.body;
        moveToMatrix(bone, newMatrixStack, () -> this.body.render(newMatrixStack, vertices, light, overlay, color));

        bone = renderer.rightLeg;
        moveToMatrix(bone, newMatrixStack, () -> {
            newMatrixStack.translate(2 / 16F, -12 / 16F,0);
            this.rightLeg.render(newMatrixStack, vertices, light, overlay, color);
        });

        bone = renderer.leftLeg;
        moveToMatrix(bone, newMatrixStack, () -> {
            newMatrixStack.translate(-2 / 16F, -12 / 16F,0);
            this.leftLeg.render(newMatrixStack, vertices, light, overlay, color);
        });
    }

    private void moveToMatrix(GeoBone from, MatrixStack matrixStack, Runnable toRender) {
        matrixStack.push();
        matrixStack.peek().getNormalMatrix().mul(from.getWorldSpaceNormal());
        matrixStack.peek().getPositionMatrix().mul(from.getWorldSpaceMatrix());
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        toRender.run();
        matrixStack.pop();
    }

}
