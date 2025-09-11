package com.astradia.utils;

import com.astradia.AstradiaClient;
import com.astradia.render.GeoBoneAccessor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.cache.object.GeoBone;

public class VentoRenderUtil {

    public static void moveToTrackedVisualMatrix(GeoBone from, MatrixStack to) {
        moveToTrackedVisualMatrix(from, to, false);
    }

    public static void moveToTrackedVisualMatrix(GeoBone from, MatrixStack to, boolean applyRotations) {
        if(!((GeoBoneAccessor) from).shouldCaptureVisualMatrix()) {
            AstradiaClient.LOGGER.warn("Bone is not tracking visual matrices!");
            return;
        }
        to.peek().getNormalMatrix().mul(from.getWorldSpaceNormal());
        to.peek().getPositionMatrix().mul(from.getWorldSpaceMatrix());
        if(applyRotations) {
            to.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            to.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        }
    }
}
