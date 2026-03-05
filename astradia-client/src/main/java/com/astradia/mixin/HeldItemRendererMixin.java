package com.astradia.mixin;

import com.astradia.renderer.entity.player.PlayerRenderer;
import com.astradia.renderer.entity.player.PlayerRendererManager;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(
            method = "renderMapHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void vento$onRenderMapHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, HumanoidArm humanoidArm, CallbackInfo ci) {
        EntityRenderer<?, ?> renderer = entityRenderDispatcher.getRenderer(minecraft.player);
        if(renderer instanceof PlayerRenderer) {
            var avatarRenderer = ((PlayerRenderer<?, ?>) renderer).original;
            Identifier identifier = this.minecraft.player.getSkin().body().texturePath();
            if (humanoidArm == HumanoidArm.RIGHT) {
                avatarRenderer.renderRightHand(poseStack, submitNodeCollector, i, identifier, this.minecraft.player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE));
            } else {
                avatarRenderer.renderLeftHand(poseStack, submitNodeCollector, i, identifier, this.minecraft.player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE));
            }
        }
        poseStack.popPose();
        ci.cancel();
    }

    @Inject(
            method = "renderPlayerArm",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void vento$onRenderPlayerArm(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, float f, float g, HumanoidArm humanoidArm, CallbackInfo ci, @Local boolean bl,  @Local AbstractClientPlayer abstractClientPlayer) {
        EntityRenderer<?, ?> renderer = entityRenderDispatcher.getRenderer(abstractClientPlayer);
        if(renderer instanceof PlayerRenderer) {
            var avatarRenderer = ((PlayerRenderer<?, ?>) renderer).original;
            Identifier identifier = abstractClientPlayer.getSkin().body().texturePath();

            if (bl) {
                avatarRenderer.renderRightHand(poseStack, submitNodeCollector, i, identifier, abstractClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE));
            } else {
                avatarRenderer.renderLeftHand(poseStack, submitNodeCollector, i, identifier, abstractClientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE));
            }
        }
        ci.cancel();
    }
}
