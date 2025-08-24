package com.astradia.mixin;

import net.bati.guilib.ClientInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientInitializer.class)
public class BatiLibMixin {
    @Inject(at = @At("HEAD"), method = "onInitializeClient()V", cancellable = true, remap = false)
    public void initialize(CallbackInfo info) {
        info.cancel();
    }
}
