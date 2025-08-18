package com.astradia.screen;

import com.astradia.AstradiaClient;
import com.astradia.player.EquipmentSlot;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class DebugOverlay {
    private static final Identifier DEBUG_LAYER = Identifier.of(AstradiaClient.MOD_ID, "debug-layer");

    public static void initialize() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, DEBUG_LAYER, DebugOverlay::render));
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        renderCosmetics(context, tickCounter);
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var player = MinecraftClient.getInstance().player;
        if(player == null) return;
        var equipment = AstradiaClient.getPlayerManager().getFromUuid(player.getUuid()).getCosmetics();
        context.drawText(textRenderer, "PLAYER COSMETICS EQUIPMENT", 5, 5, 0xFFFFFFFF, true);
        int i = 0;
        var entries = equipment.getEquippedInventory();
        context.getMatrices().push();
        context.getMatrices().scale(0.8f, 0.8f, 1);
        for (EquipmentSlot entry : entries) {
            context.drawText(textRenderer, entry.getBodyPart().name() + "," + entry.getSlotType().name() + ": " + (entry.getCachedCosmetic() == null ? "null" : entry.getCachedCosmetic().getName()), 7, 16 + 9 * i++, 0xFFFFFFFF, true);
        }
        context.getMatrices().pop();
    }

    private static void renderCosmetics(DrawContext context, RenderTickCounter tickCounter) {

    }
}
