package com.astradia.screen;

import com.astradia.AstradiaClient;
import com.astradia.ClientPlayerCosmeticManager;
import com.astradia.enums.BodyPart;
import com.astradia.player.ClientEquipmentSlot;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

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
        var equipment = ClientPlayerCosmeticManager.INSTANCE.getFrom(player.getUuid());
        context.drawText(textRenderer, "PLAYER COSMETICS EQUIPMENT", 5, 5, 0xFFFFFFFF, true);
        int i = 0;
        var entries = equipment.getEquipment().entrySet();
        context.getMatrices().push();
        context.getMatrices().scale(0.8f, 0.8f, 1);
        for (Map.Entry<BodyPart, List<ClientEquipmentSlot>> entry : entries) {
            context.drawText(textRenderer, entry.getKey().name(), 7, 16 + 9 * i++, 0xFFFFFFFF, true);
            for (ClientEquipmentSlot clientEquipmentSlot : entry.getValue()) {
                var cosmetic = clientEquipmentSlot.getCachedCosmetic();
                context.drawText(textRenderer, clientEquipmentSlot.getSlotType().name() + ": " + (cosmetic.getCached() == null ? cosmetic.getId() + "" : cosmetic.getCached().getName()), 11, 16 + 9 * i++, 0xFFFFFFFF, true);

            }
        }
        context.getMatrices().pop();
    }

    private static void renderCosmetics(DrawContext context, RenderTickCounter tickCounter) {

    }
}
