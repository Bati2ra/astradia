package com.astradia.screen;

import com.astradia.AstradiaClient;
import com.astradia.ClientCosmeticStore;
import com.astradia.api.CosmeticProperty;
import com.astradia.player.ClientCosmeticSlot;
import com.astradia.pojo.ClientCosmeticInfo;
import com.astradia.utils.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class DebugOverlay {
    private static final Identifier DEBUG_LAYER = Identifier.of(AstradiaClient.MOD_ID, "debug-layer");
    private static DebugMode debug = DebugMode.OFF;

    public static void initialize() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, DEBUG_LAYER, DebugOverlay::render));
    }

    public static void nextMode() {
        var values = DebugMode.values();
        int index = debug.ordinal();
        int nextIndex = index + 1;
        if(nextIndex >= values.length) nextIndex = 0;
        debug = values[nextIndex];

    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        switch (debug) {
            case PlayerData -> renderPlayerData(context, tickCounter);
            case CosmeticStore -> renderCosmeticStore(context, tickCounter);
        }
    }

    private static void renderPlayerData(DrawContext context, RenderTickCounter tickCounter) {
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var player = MinecraftClient.getInstance().player;
        if(player == null) return;
        var playerData = AstradiaClient.getPlayerManager().getFromPlayer(player);
        int i = 0;
        context.drawText(textRenderer, "Player Data: " + (playerData == null ? "No data" : "Loaded"), 5, 5 + i++ * 9, 0xFFFFFFFF, true);

        if(playerData == null) return;
        var equipment = playerData.getCosmetics();
        context.drawText(textRenderer, "  Player Cosmetics", 5, 5 + 9 * i++, 0xFFFFFFFF, true);

        context.getMatrices().push();
        context.getMatrices().scale(0.8f, 0.8f, 1);
        for (Map.Entry<Identifier, ClientCosmeticSlot> entry : equipment.getEquippedInventory().entrySet()) {
            var cosmeticData = entry.getValue().getCosmeticData();
            context.drawText(textRenderer, String.format("Slot '%s': %s", entry.getKey(), cosmeticData == null ? "Empty" : cosmeticData.toJson().toString()), 7, 16 + 9 * i++, 0xFFFFFFFF, true);
        }
        context.getMatrices().pop();
    }

    private static int scrollOffset = 0;
    private static final int SCROLL_STEP = 10; // píxeles por tick
    private static final int LINE_HEIGHT = 9;   // altura de línea del textRenderer

    private static void handleScroll() {
        MinecraftClient client = MinecraftClient.getInstance();
        long window = client.getWindow().getHandle();

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            scrollOffset -= SCROLL_STEP;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            scrollOffset += SCROLL_STEP;
        }

        if (scrollOffset < 0) scrollOffset = 0;
    }

    private static void renderCosmeticStore(DrawContext context, RenderTickCounter tickCounter) {
        handleScroll(); // actualizar scroll antes de dibujar

        var textRenderer = MinecraftClient.getInstance().textRenderer;
        int y = 5 - scrollOffset;

        context.drawText(textRenderer, "Cosmetic Store Data:", 5, y, 0xFFFFFFFF, true);
        y += LINE_HEIGHT;

        var store = ClientCosmeticStore.INSTANCE;
        if (!store.isReady()) return;

        for (ClientCosmeticInfo value : store.getAll().values()) {
            JsonObject data = value.toJson();

            // Renderizar cada propiedad del JSON
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                String key = entry.getKey();
                JsonElement el = entry.getValue();

                // Si es un array (como properties), renderizamos cada elemento
                if (el.isJsonArray()) {
                    context.drawText(textRenderer, key + ":", 10, y, 0xFFFFFFAA, true);
                    y += LINE_HEIGHT;
                    JsonArray array = el.getAsJsonArray();
                    for (JsonElement item : array) {
                        String line = GsonUtils.GSON.toJson(item);
                        context.drawText(textRenderer, "  " + line, 15, y, 0xFFFFFFFF, true);
                        y += LINE_HEIGHT;
                    }
                } else {
                    context.drawText(textRenderer, key + ": " + el.toString(), 10, y, 0xFFFFFFFF, true);
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
