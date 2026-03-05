package com.astradia.mixin;

import com.astradia.renderer.entity.state.VentoAvatarRenderState;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class PlayerEntityRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @Inject(at = @At(value = "TAIL"), method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V")
    private void astradia$updateRenderState(AvatarlikeEntity playerLikeEntity, AvatarRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if(playerEntityRenderState instanceof VentoAvatarRenderState ventoAvatarRenderState) {
            ventoAvatarRenderState.setUuid(playerLikeEntity.getUUID());
        }
    }
}
