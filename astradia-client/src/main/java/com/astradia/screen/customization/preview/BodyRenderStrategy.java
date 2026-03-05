package com.astradia.screen.customization.preview;

import com.astradia.screen.util.FakePlayer;
import com.astradia.screen.util.RenderUtil;
import net.bati.miniui.layout.ComputedLayout;
import net.bati.miniui.rendering.RenderPassInfo;

public class BodyRenderStrategy implements CosmeticRenderStrategy {

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

        int scale = (int) (Math.min(w, h) * 0.9f);

        float mouseX = 0;
        float mouseY = 0;

        float offsetX = 0f;
        float offsetY = 0.1f;
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
                -35,
                -15,
                player
        );
    }
}