package com.astradia.screen.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderUtil {
    public static void renderEntityInUI(
            GuiGraphics guiGraphics,
            int left,
            int top,
            int right,
            int bottom,
            int scale,
            float verticalOffset,
            float mouseX,
            float mouseY,
            float modelOffsetX,
            float modelOffsetY,
            float extraGlobalYaw,   // 🔥 NUEVO parámetro
            float extraGlobalPitch,   // 🔥 NUEVO parámetro
            LivingEntity entity
    ) {
        // Pitch como quaternion separado, igual que vanilla
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(extraGlobalPitch * 0.017453292F);
        quaternionf.mul(quaternionf2);

        EntityRenderState renderState = extractRenderState(entity);

        if (renderState instanceof LivingEntityRenderState livingState) {
            // Yaw aplicado al modelo igual que vanilla (no como rotación global)
            livingState.bodyRot = 180.0F + extraGlobalYaw;
            float headYaw = entity.getYHeadRot();
            float pitch = entity.getXRot();
            livingState.yRot = headYaw;
            livingState.xRot = pitch;

            livingState.boundingBoxWidth /= livingState.scale;
            livingState.boundingBoxHeight /= livingState.scale;
            livingState.scale = 1.0F;
        }

        // Pivot central igual que vanilla + offset visual
        Vector3f translation = new Vector3f(
                modelOffsetX,
                renderState.boundingBoxHeight / 2.0F + verticalOffset + modelOffsetY,
                0.0F
        );

        guiGraphics.submitEntityRenderState(
                renderState,
                (float) scale,
                translation,
                quaternionf,
                quaternionf2,
                left,
                top,
                right,
                bottom
        );
    }

    private static EntityRenderState extractRenderState(LivingEntity livingEntity) {
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> entityRenderer = entityRenderDispatcher.getRenderer(livingEntity);
        EntityRenderState entityRenderState = entityRenderer.createRenderState(livingEntity, 1.0F);
        entityRenderState.lightCoords = 15728880;
        entityRenderState.shadowPieces.clear();
        entityRenderState.outlineColor = 0;
        return entityRenderState;
    }
}
