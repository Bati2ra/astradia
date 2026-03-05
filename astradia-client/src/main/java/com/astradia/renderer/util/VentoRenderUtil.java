package com.astradia.renderer.util;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

public class VentoRenderUtil {
    public static void transformToBoneFixed(PoseStack poseStack, GeoBone bone) {
        final List<GeoBone> boneQueue = new ObjectArrayList<>();
        GeoBone parent = bone;

        boneQueue.add(bone);

        while ((parent = parent.parent()) != null) {
            boneQueue.add(parent);
        }
        for (int i = boneQueue.size() - 1; i >= 0; i--) {
            RenderUtil.prepMatrixForBone(poseStack, boneQueue.get(i));
        }
    }

    public static void transformToBoneWithoutScale(PoseStack poseStack, GeoBone bone) {
        final List<GeoBone> boneQueue = new ObjectArrayList<>();
        GeoBone parent = bone;

        boneQueue.add(bone);

        while ((parent = parent.parent()) != null) {
            boneQueue.add(parent);
        }
        for (int i = boneQueue.size() - 1; i >= 0; i--) {
            prepMatrixForBoneWithoutScale(poseStack, boneQueue.get(i));
        }/*
        for (GeoBone bone2 : boneQueue) {
            prepMatrixForBoneWithoutScale(poseStack, bone2);
        }*/
    }

    public static void prepMatrixForBoneWithoutScale(PoseStack poseStack, GeoBone bone) {
        if (bone.frameSnapshot != null)
            bone.frameSnapshot.translate(poseStack);

        RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);

        // OMITIR: bone.frameSnapshot.scale(poseStack);

        bone.translateAwayFromPivotPoint(poseStack);
    }
}
