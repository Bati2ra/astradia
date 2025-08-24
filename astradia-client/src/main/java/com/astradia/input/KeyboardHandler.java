package com.astradia.input;

import com.astradia.screen.DebugOverlay;
import com.astradia.screen.WardrobeScreen;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeyboardHandler {
    public static void handle(long window, int key, int scancode, int i, int j) {
        if(!checks(window, MinecraftClient.getInstance()))
            return;

        if(i == 0 && key == GLFW.GLFW_KEY_KP_1) {
            DebugOverlay.nextMode();
        }

        if(i == 0 && key == GLFW.GLFW_KEY_V) {
            MinecraftClient.getInstance().setScreen(new WardrobeScreen(null));
        }
    }

    private static boolean checks(long window, MinecraftClient mc) {
        return (window == MinecraftClient.getInstance().getWindow().getHandle()) &&
                mc != null && mc.world != null && mc.player != null && mc.currentScreen == null;

    }
}
