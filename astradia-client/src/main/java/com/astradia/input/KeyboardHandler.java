package com.astradia.input;

import com.astradia.VentoClient;
import com.astradia.screen.DebugOverlay;
import com.astradia.screen.PlayerCustomizationScreen;
import com.astradia.screen.ProportionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;


public class KeyboardHandler {
    public static void handle(long window, int action, KeyEvent input) {
        if(!checks(window, Minecraft.getInstance()))
            return;

        if(action == 0 && input.input() == GLFW.GLFW_KEY_KP_1) {
            DebugOverlay.nextMode();
        }

        if(action == 0 && input.input() == GLFW.GLFW_KEY_V) {
            Minecraft.getInstance().setScreen(new PlayerCustomizationScreen(null));
        }
    }

    private static boolean checks(long window, Minecraft mc) {
        return (window == Minecraft.getInstance().getWindow().handle()) &&
                mc != null && mc.level != null && mc.player != null && mc.screen == null;

    }
}
