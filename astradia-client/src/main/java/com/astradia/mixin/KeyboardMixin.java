package com.astradia.mixin;

import com.astradia.input.KeyboardHandler;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(at = @At("HEAD"), method = "onKey(JIIII)V")
    private void keyboardInject(long window, int key, int scancode, int i, int j, CallbackInfo info) {
        KeyboardHandler.handle(window,key,scancode,i,j);
    }
}
