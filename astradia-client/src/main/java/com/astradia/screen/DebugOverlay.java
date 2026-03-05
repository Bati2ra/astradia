package com.astradia.screen;

import com.astradia.VentoClient;
import com.astradia.ClientCosmeticStore;
import com.astradia.player.ClientCosmeticSlot;
import com.astradia.pojo.ClientCosmeticDefinition;
import com.astradia.utils.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class DebugOverlay {
    private static final Identifier DEBUG_LAYER = Identifier.fromNamespaceAndPath(VentoClient.MOD_ID, "debug-layer");
    private static DebugMode debug = DebugMode.OFF;

    public static void initialize() {
        HudRenderCallback.EVENT.register(DebugOverlay::render);
        //HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, DEBUG_LAYER, DebugOverlay::render));
    }

    public static void nextMode() {
        var values = DebugMode.values();
        int index = debug.ordinal();
        int nextIndex = index + 1;
        if(nextIndex >= values.length) nextIndex = 0;
        debug = values[nextIndex];

    }

    private static void render(GuiGraphics context, DeltaTracker tickCounter) {
        switch (debug) {
            case PlayerData -> renderPlayerData(context, tickCounter);
            case CosmeticStore -> renderCosmeticStore(context, tickCounter);
        }
    }

    private static void renderPlayerData(GuiGraphics context, DeltaTracker tickCounter) {
        var textRenderer = Minecraft.getInstance().font;
        var player = Minecraft.getInstance().player;
        if(player == null) return;
        var playerData = VentoClient.getPlayerManager().getFromPlayer(player);
        int i = 0;
        context.drawString(textRenderer, "Player Data: " + (playerData == null ? "No data" : "Loaded"), 5, 5 + i++ * 9, 0xFFFFFFFF, true);

        if(playerData == null) return;
        var equipment = playerData.getCosmetics();
        context.drawString(textRenderer, "  Player Cosmetics", 5, 5 + 9 * i++, 0xFFFFFFFF, true);

        context.pose().pushMatrix();
        context.pose().scale(0.8f, 0.8f);
        for (Map.Entry<Identifier, ClientCosmeticSlot> entry : equipment.getSlots().entrySet()) {
            var cosmeticData = entry.getValue().getEquipped();
            context.drawString(textRenderer, String.format("Slot '%s': %s", entry.getKey(), cosmeticData == null ? "Empty" : cosmeticData.toJson().toString()), 7, 16 + 9 * i++, 0xFFFFFFFF, true);
        }
        context.pose().popMatrix();
    }

    private static int scrollOffset = 0;
    private static final int SCROLL_STEP = 10; // píxeles por tick
    private static final int LINE_HEIGHT = 9;   // altura de línea del textRenderer

    private static void handleScroll() {
        Minecraft client = Minecraft.getInstance();
        long window = client.getWindow().handle();

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            scrollOffset -= SCROLL_STEP;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            scrollOffset += SCROLL_STEP;
        }

        if (scrollOffset < 0) scrollOffset = 0;
    }

    private static void renderCosmeticStore(GuiGraphics context, DeltaTracker tickCounter) {
        handleScroll(); // actualizar scroll antes de dibujar

        var textRenderer = Minecraft.getInstance().font;
        int y = 5 - scrollOffset;

        context.drawString(textRenderer, "Cosmetic Store Data:", 5, y, 0xFFFFFFFF, true);
        y += LINE_HEIGHT;

        var store = ClientCosmeticStore.INSTANCE;
        if (!store.isReady()) return;

        for (ClientCosmeticDefinition value : store.getAll().values()) {
            JsonObject data = value.toJson();

            // Renderizar cada propiedad del JSON
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                String key = entry.getKey();
                JsonElement el = entry.getValue();

                // Si es un array (como properties), renderizamos cada elemento
                if (el.isJsonArray()) {
                    context.drawString(textRenderer, key + ":", 10, y, 0xFFFFFFAA, true);
                    y += LINE_HEIGHT;
                    JsonArray array = el.getAsJsonArray();
                    for (JsonElement item : array) {
                        String line = GsonUtils.GSON.toJson(item);
                        context.drawString(textRenderer, "  " + line, 15, y, 0xFFFFFFFF, true);
                        y += LINE_HEIGHT;
                    }
                } else {
                    context.drawString(textRenderer, key + ": " + el.toString(), 10, y, 0xFFFFFFFF, true);
                    y += LINE_HEIGHT;
                }
            }

            y += LINE_HEIGHT; // separación entre cosméticos
        }
    }

    enum DebugMode {
        OFF,
        PlayerData,
        CosmeticStore
    }
}
