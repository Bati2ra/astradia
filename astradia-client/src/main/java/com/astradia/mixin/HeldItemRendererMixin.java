package com.astradia.mixin;

import com.astradia.render.player.PlayerRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;"))
    private <T extends Entity> EntityRenderer<? super T, ?> drgnsAscnt$renderArmHoldingItem(EntityRenderDispatcher instance, T entityrenderer) {
        var renderer = (PlayerRenderer)  instance.getRenderer(this.client.player);
        return (EntityRenderer<? super T, ?>) renderer.original;
    }

    @Redirect(method = "renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Arm;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;"))
    private <T extends Entity> EntityRenderer<? super T, ?> drgnsAscnt$renderPlayerArm(EntityRenderDispatcher instance, T entityrenderer) {
        var renderer = (PlayerRenderer)  instance.getRenderer(this.client.player);
        return (EntityRenderer<? super T, ?>) renderer.original;
    }
}
