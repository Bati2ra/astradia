package com.astradia.screen.customization.preview;

import com.astradia.screen.util.FakePlayer;
import net.bati.miniui.layout.ComputedLayout;
import net.bati.miniui.rendering.RenderPassInfo;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CosmeticPreviewRenderer {

    public static final Map<Identifier, CosmeticRenderStrategy> renderStrategies = new HashMap<>();
    public static final HeadRenderStrategy DEFAULT_RENDER_STRATEGY = new HeadRenderStrategy();

    static {
        renderStrategies.put(
                Identifier.fromNamespaceAndPath("head", "hair"),
                DEFAULT_RENDER_STRATEGY);
        renderStrategies.put(
                Identifier.fromNamespaceAndPath("torso", "accessory"),
                new BodyRenderStrategy());
    }

    private FakePlayer player;
    private CosmeticRenderStrategy strategy;

    public CosmeticPreviewRenderer(FakePlayer player) {
        this.player = player;
    }

    public static CosmeticRenderStrategy getStrategyForCategory(Identifier category) {
        return renderStrategies.getOrDefault(category, DEFAULT_RENDER_STRATEGY);
    }

    public void setStrategy(CosmeticRenderStrategy strategy) {
        this.strategy = strategy;
    }

    public void setPlayer(FakePlayer player) {
        this.player = player;
    }

    public void render(RenderPassInfo rp, ComputedLayout.Bounds bounds) {
        if (strategy == null) return;

        strategy.render(rp, player, bounds);
    }
}