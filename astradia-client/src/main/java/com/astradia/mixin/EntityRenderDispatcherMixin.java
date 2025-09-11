package com.astradia.mixin;

import com.astradia.render.player.PlayerRendererManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Shadow
    private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers;

    @Inject(at = @At("HEAD"), method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", cancellable = true)
    public void vento$getRenderer(Entity entity, CallbackInfoReturnable<EntityRenderer<? super Entity, ?>> cir) {
        if(entity instanceof AbstractClientPlayerEntity player) {
            cir.setReturnValue(PlayerRendererManager.INSTANCE.getRenderer(player));
        }
    }

    @Inject(at = @At("HEAD"), method = "getRenderer(Lnet/minecraft/client/render/entity/state/EntityRenderState;)Lnet/minecraft/client/render/entity/EntityRenderer;", cancellable = true)
    public <S extends EntityRenderState> void vento$getRenderer(S state, CallbackInfoReturnable<EntityRenderer<? super Entity, ?>> cir) {
        if(state instanceof PlayerEntityRenderState playerEntityRenderState) {
            cir.setReturnValue(PlayerRendererManager.INSTANCE.getRenderer(playerEntityRenderState));
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "reload(Lnet/minecraft/resource/ResourceManager;)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void vento$reload(ResourceManager manager, CallbackInfo ci, EntityRendererFactory.Context context) {
        PlayerRendererManager.INSTANCE.reload(manager, context, modelRenderers);
    }
}
