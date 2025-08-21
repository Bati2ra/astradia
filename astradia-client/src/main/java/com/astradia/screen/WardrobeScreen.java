package com.astradia.screen;

import net.bati.guilib.gui.components.Container;
import net.bati.guilib.gui.screen.AdvancedScreen;
import net.bati.guilib.utils.font.TextUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class WardrobeScreen extends AdvancedScreen {
    public WardrobeScreen(@Nullable Text title) {
        super(title);
    }

    @Override
    public void build() {
        Container
    }

    @Override
    public void update(DrawContext drawContext, int i, int i1, float v) {
        TextUtils.drawText("BOENAS", 0, 0, 1, 16777215, true, false, drawContext);
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }

    @Override
    public boolean shouldGuiCloseOnEsc() {
        return true;
    }
}
