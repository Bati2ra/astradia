package com.astradia.screen.customization.preview;

import com.astradia.screen.util.FakePlayer;
import net.bati.miniui.layout.ComputedLayout;
import net.bati.miniui.rendering.RenderPassInfo;

public interface CosmeticRenderStrategy {
    void render(
            RenderPassInfo rp,
            FakePlayer player,
            ComputedLayout.Bounds bounds
    );
}