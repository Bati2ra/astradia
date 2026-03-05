package com.astradia.screen.customization.preview;

import com.astradia.screen.util.FakePlayer;
import com.astradia.screen.util.RenderUtil;
import net.bati.miniui.layout.ComputedLayout;
import net.bati.miniui.rendering.RenderPassInfo;

public class HeadRenderStrategy implements CosmeticRenderStrategy {

    @Override
    public void render(RenderPassInfo rp,
                       FakePlayer player,
                       ComputedLayout.Bounds bounds) {

        float x = bounds.getX();
        float y = bounds.getY();
        float w = bounds.getWidth();
        float h = bounds.getHeight();

        int left = (int) x;
        int top = (int) y;
        int right = (int) (x + w);
        int bottom = (int) (y + h);

        int scale = (int) (Math.min(w, h) * 1f);

        float mouseX = 0;
        float mouseY = 0;

        float offsetX = 0f;
        float offsetY = 0.5f;
        player.setYHeadRot(0);
        player.setXRot(0);
        RenderUtil.renderEntityInUI(
                rp.graphics,
                left,
                top,
                right,
                bottom,
                scale,
                0f,
                mouseX,
                mouseY,
                offsetX,
                offsetY,
                -30,
                -35,
                player
        );
    }
}