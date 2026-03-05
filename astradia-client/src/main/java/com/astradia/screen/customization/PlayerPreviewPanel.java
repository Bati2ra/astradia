package com.astradia.screen.customization;

import com.astradia.screen.util.FakePlayer;
import net.bati.miniui.layout.LayoutConstraints;
import net.bati.miniui.layout.MeasureResult;
import net.bati.miniui.layout.SizeConstraint;
import net.bati.miniui.rendering.RenderPassInfo;
import net.bati.miniui.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;

public class PlayerPreviewPanel extends Widget {

    private final CustomizationViewModel viewModel;
    private final FakePlayer fakePlayer;
    public PlayerPreviewPanel(CustomizationViewModel viewModel) {
        super("player-preview");
        this.viewModel = viewModel;
        setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.percentage(0.4f))
                .withHeight(SizeConstraint.fillParent()));
        ClientLevel level = Minecraft.getInstance().level;
        fakePlayer = new FakePlayer(level);
    }

    @Override
    protected MeasureResult measureContent(float v, float v1) {
        return MeasureResult.ZERO;
    }

    @Override
    protected void renderContent(RenderPassInfo rp) {
        super.renderContent(rp);
        if (getComputedLayout() == null) return;

        var content = getComputedLayout().getContentBounds();

        float x = content.getX();
        float y = content.getY();
        float w = content.getWidth();
        float h = content.getHeight();

        int left = (int) x;
        int top = (int) y;
        int right = (int) (x + w);
        int bottom = (int) (y + h);

        float baseSize = Math.min(w, h);
        int scale = (int) (baseSize * 0.45f);
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                rp.graphics,
                left,
                top,
                right,
                bottom,
                scale,
                0,
                rp.mouseX,
                rp.mouseY,
                fakePlayer
        );

    }
}