package com.astradia.mixin;

import com.astradia.utils.AstradiaPlayerEntityRenderState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(at = @At(value = "TAIL"), method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V")
    private void astradia$updateRenderState(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if(playerEntityRenderState instanceof AstradiaPlayerEntityRenderState astradiaPlayerEntityRenderState) {
            astradiaPlayerEntityRenderState.setUuid(abstractClientPlayerEntity.getUuid());
            astradiaPlayerEntityRenderState.setPartialTick(f);
        }
    }
}
