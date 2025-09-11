package com.astradia.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin {

    @SuppressWarnings("all")
    @WrapOperation(
            method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIFLorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;getAndUpdateRenderState(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/entity/state/EntityRenderState;")
    )
    private static EntityRenderState wrapRenderState(EntityRenderer<?, ?> instance, Entity entity, float tickProgress, Operation<EntityRenderState> original) {
        if(instance instanceof GeoEntityRenderer geoEntityRenderer) {
            return geoEntityRenderer.getAndUpdateRenderState(entity, tickProgress);
        } else {
            return original.call(instance, entity, tickProgress);
        }
    }
}
