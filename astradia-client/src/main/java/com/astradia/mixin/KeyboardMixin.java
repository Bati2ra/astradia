package com.astradia.mixin;

import com.astradia.input.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.KeyboardHandler.class)
public class KeyboardMixin {
    @Inject(at = @At("HEAD"), method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V")
    private void keyboardInject(long window, int action, KeyEvent input, CallbackInfo ci) {
        KeyboardHandler.handle(window, action, input);
    }
}
