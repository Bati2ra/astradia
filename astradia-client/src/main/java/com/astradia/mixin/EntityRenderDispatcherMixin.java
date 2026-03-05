package com.astradia.mixin;

import com.astradia.renderer.entity.player.PlayerRendererManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.PlayerModelType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Shadow
    private Map<PlayerModelType, AvatarRenderer<AbstractClientPlayer>> playerRenderers = Map.of();

    @Inject(at = @At("HEAD"), method = "getRenderer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", cancellable = true)
    public void vento$getRenderer(Entity entity, CallbackInfoReturnable<EntityRenderer<? super Entity, ?>> cir) {
        if(entity instanceof AbstractClientPlayer player) {
            cir.setReturnValue(PlayerRendererManager.INSTANCE.getRenderer(player));
        }
    }

    @Inject(at = @At("HEAD"), method = "getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", cancellable = true)
    public <S extends EntityRenderState> void vento$getRenderer(S state, CallbackInfoReturnable<EntityRenderer<? super Entity, ?>> cir) {
        if(state instanceof AvatarRenderState playerEntityRenderState) {
            cir.setReturnValue(PlayerRendererManager.INSTANCE.getRenderer(playerEntityRenderState));
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager;)V")
    private void vento$reload(ResourceManager manager, CallbackInfo ci, @Local EntityRendererProvider.Context context) {
        PlayerRendererManager.INSTANCE.reload(manager, context, playerRenderers);
    }
}
